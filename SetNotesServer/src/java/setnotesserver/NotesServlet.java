/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesserver;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.zip.DataFormatException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
        //todo
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
        
        //todo
        
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
            Node keyNode = noteNode.getElementsByTagName("publicKey").item(0);
            Node deletedNode = noteNode.getElementsByTagName("isDeleted").item(0);
            Node noteDataNode = noteNode.getElementsByTagName("noteData").item(0);

            note.setCreateDate(createNode.getTextContent());
            note.setEditDate(editNode.getTextContent());
            note.setNoteId(idNode.getTextContent());
            note.setUserId(keyNode.getTextContent());
            note.setIsDeleted(deletedNode.getTextContent());
            if(!note.isDeleted()){
                note.setNoteData(noteDataNode.getTextContent());
            }
            
            return note;
        }catch(ParserConfigurationException | IOException | SAXException | DOMException | ParseException | NullPointerException | NumberFormatException ex){
            throw new DataFormatException();
        }
    }

}
