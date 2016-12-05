/*  
*  File NoteServlet.java
*  Project SetNotesServer
*  Authors Adam Currie, Dylan O'Neill
*  Date 2016-11-8
*/
package setnotesserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.DataFormatException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import shared.ECDSAUtil;

/*
 * Name     NotesServlet
 * Purpose  Servlet for adding and retrieving encrypted notes.
 */
public class NotesServlet extends HttpServlet{

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo(){
        return "Set Notes Server";
    }// </editor-fold>
    
    /*
     * Method                           doPut
     * Description                      put request handler
     * Params           
     *  HttpServletRequest request      the request object
     *  HttpServletResponse response    the response object
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        try{
            UserNote note = getNoteFromRequest(request);
            
            if(note.isDeleted()){
                //DELETE
                Database.delete(note.getUserId(), note.getNoteId(), note.getSignature());
                response.setStatus(HttpServletResponse.SC_OK);
            }else{
                //ADD OR UPDATE
                Database.addOrUpdate(note);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }catch(DataFormatException | SignatureException | InvalidKeyException ex){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }catch(SQLException ex){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * Method                           doGet
     * Description                      get request handler
     * Params           
     *  HttpServletRequest request      the request object
     *  HttpServletResponse response    the response object
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{            
        try{
            Map<java.lang.String,java.lang.String[]> map = request.getParameterMap();
            String userId = request.getParameter("publicKey");
            
            ArrayList<UserNote> notes = Database.getAllNotes(userId);
            Document doc = getXmlFromNotes(notes);
            
            //get xml bytes
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);
            transformer.transform(new DOMSource(doc), result);
            byte[] dataBytes = bos.toByteArray();
            
            //send
            response.getOutputStream().write(dataBytes);
            
            response.setStatus(HttpServletResponse.SC_OK);
            
        }catch(NullPointerException ex){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }catch(SQLException|ParserConfigurationException|TransformerException ex){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    /*
     * Method                       getXmlFromNotes
     * Description                  creates an xml document froma list of notes
     * Params           
     *  ArrayList<UserNote> notes   the notes to create xml for
     * Returns          
     *  Document                    the created xml document
     */
    private static Document getXmlFromNotes(ArrayList<UserNote> notes) throws ParserConfigurationException{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        Element noteListRootNode = doc.createElement("notes");
        doc.appendChild(noteListRootNode);
        
        for(UserNote userNote : notes){
            //NOTE ROOT
            Element noteRootNode = doc.createElement("noteRoot");
                //NOTE
                Element noteNode = doc.createElement("note");
                    Element noteId = doc.createElement("noteId");
                    noteId.appendChild(doc.createTextNode(Long.toString(userNote.getNoteId())));
                    noteNode.appendChild(noteId);

                    Element createDate = doc.createElement("createDate");
                    createDate.appendChild(doc.createTextNode(userNote.getCreateDateString()));
                    noteNode.appendChild(createDate);

                    Element editDate = doc.createElement("editDate");
                    editDate.appendChild(doc.createTextNode(userNote.getEditDateString()));
                    noteNode.appendChild(editDate);

                    Element isDeleted = doc.createElement("isDeleted");
                    isDeleted.appendChild(doc.createTextNode(String.valueOf(userNote.isDeleted())));
                    noteNode.appendChild(isDeleted);

                    Element publicKeyNode = doc.createElement("publicKey");
                    publicKeyNode.appendChild(doc.createTextNode(userNote.getUserId()));
                    noteNode.appendChild(publicKeyNode);

                    if(userNote.getNoteBody() != null && !userNote.getNoteBody().isEmpty()){
                        Element noteDataNode = doc.createElement("noteData");
                        noteDataNode.appendChild(doc.createTextNode(userNote.getNoteBody()));
                        noteNode.appendChild(noteDataNode);
                    }
                noteRootNode.appendChild(noteNode);
                
                //SIGNATURE
                Element signatureNode = doc.createElement("signature");
                    signatureNode.appendChild(doc.createTextNode(userNote.getSignature()));
                noteRootNode.appendChild(signatureNode);
            noteListRootNode.appendChild(noteRootNode);
        }
        
        return doc;
    }
    
    /*
     * Method                       getNoteFromRequest
     * Description                  rerieve a UserNote from a request object
     * Params           
     *  HttpServletRequest request  the request object
     * Returns          
     *  UserNote                    the creates note
     */
    private static UserNote getNoteFromRequest(HttpServletRequest request) throws DataFormatException, SignatureException, InvalidKeyException{
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(request.getInputStream());
            Element root = doc.getDocumentElement();
            Element noteNode = (Element)root.getElementsByTagName("note").item(0);
            Element signatureNode = (Element)root.getElementsByTagName("signature").item(0);

            UserNote note = new UserNote();
            Node createNode = noteNode.getElementsByTagName("createDate").item(0);
            Node editNode = noteNode.getElementsByTagName("editDate").item(0);
            Node idNode = noteNode.getElementsByTagName("noteId").item(0);
            Node deletedNode = noteNode.getElementsByTagName("isDeleted").item(0);
            Node noteDataNode = noteNode.getElementsByTagName("noteData").item(0);
            Node userIdNode = noteNode.getElementsByTagName("publicKey").item(0);
            
            note.setUserId(userIdNode.getTextContent());
            note.setCreateDate(createNode.getTextContent());
            note.setEditDate(editNode.getTextContent());
            note.setNoteId(idNode.getTextContent());
            note.setIsDeleted(deletedNode.getTextContent());
            note.setSignature(signatureNode.getTextContent());
            if(!note.isDeleted()){
                note.setNoteBody(noteDataNode.getTextContent());
            }
            
            //check signature
            ECPublicKeyParameters key = ECDSAUtil.base64ToPublicKey(note.getUserId());
            if(ECDSAUtil.checkSignature(key, noteNode.getTextContent(), note.getSignature())){
                return note;
            }else{
                throw new SignatureException("Invalid signature.");
            }
        }catch(ParserConfigurationException | IOException | SAXException | DOMException | ParseException | NullPointerException | NumberFormatException ex){
            throw new DataFormatException();
        }
    }

}
