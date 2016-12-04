/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Adam
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

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        try{
            UserNote note = getNoteFromRequest(request);
            
            if(note.isDeleted()){
                //todo: check signature
                Database.delete(note.getUserId(), note.getNoteId());
                response.setStatus(HttpServletResponse.SC_OK);
            }else{
                //todo: check signature
                Database.addOrUpdate(note);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }catch(DataFormatException ex){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }catch(SQLException ex){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }   
    }

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
    
    private static Document getXmlFromNotes(ArrayList<UserNote> notes) throws ParserConfigurationException{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        Element root = doc.createElement("notes");
        doc.appendChild(root);
        
        for(UserNote note : notes){
            Element noteNode = doc.createElement("note");
            
            //add fields
            Element noteId = doc.createElement("noteId");
            noteId.appendChild(doc.createTextNode(Long.toString(note.getNoteId())));
            noteNode.appendChild(noteId);

            Element createDate = doc.createElement("createDate");
            createDate.appendChild(doc.createTextNode(note.getCreateDateString()));
            noteNode.appendChild(createDate);

            Element editDate = doc.createElement("editDate");
            editDate.appendChild(doc.createTextNode(note.getEditDateString()));
            noteNode.appendChild(editDate);

            Element isDeleted = doc.createElement("isDeleted");
            isDeleted.appendChild(doc.createTextNode(String.valueOf(note.isDeleted())));
            noteNode.appendChild(isDeleted);

            Element publicKeyNode = doc.createElement("publicKey");
            publicKeyNode.appendChild(doc.createTextNode(note.getUserId()));
            noteNode.appendChild(publicKeyNode);
            
            if(note.getNoteBody() != null && !note.getNoteBody().isEmpty()){
                Element noteDataNode = doc.createElement("noteData");
                noteDataNode.appendChild(doc.createTextNode(note.getNoteBody()));
                noteNode.appendChild(noteDataNode);
            }
            
            root.appendChild(noteNode);
        }
        
        return doc;
    }
    
    private static UserNote getNoteFromRequest(HttpServletRequest request) throws DataFormatException{
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(request.getInputStream());
            Element noteNode = doc.getDocumentElement();

            UserNote note = new UserNote();
            Node createNode = noteNode.getElementsByTagName("createDate").item(0);
            Node editNode = noteNode.getElementsByTagName("editDate").item(0);
            Node idNode = noteNode.getElementsByTagName("noteId").item(0);
            Node deletedNode = noteNode.getElementsByTagName("isDeleted").item(0);
            Node noteDataNode = noteNode.getElementsByTagName("noteData").item(0);

            note.setCreateDate(createNode.getTextContent());
            note.setEditDate(editNode.getTextContent());
            note.setNoteId(idNode.getTextContent());
            note.setIsDeleted(deletedNode.getTextContent());
            if(!note.isDeleted()){
                note.setNoteBody(noteDataNode.getTextContent());
            }
            
            return note;
        }catch(ParserConfigurationException | IOException | SAXException | DOMException | ParseException | NullPointerException | NumberFormatException ex){
            throw new DataFormatException();
        }
    }

}
