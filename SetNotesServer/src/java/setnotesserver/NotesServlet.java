/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesserver;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        UserNote note = new UserNote();
        
        try{
            note.setCreateDate(request.getParameter("createDate"));
            note.setEditDate(request.getParameter("editDate"));
            note.setNoteId(request.getParameter("noteId"));
            note.setUserId(request.getParameter("publicKey"));
            note.setIsDeleted(request.getParameter("isDeleted"));
            if(note.isDeleted()){
                //todo: check signature
                Database.delete(note.getUserId(), note.getNoteId());
                response.setStatus(HttpServletResponse.SC_OK);
            }else{
                note.setNoteData(request.getParameter("noteData"));
                //todo: check signature
                Database.addOrUpdate(note);
                response.setStatus(HttpServletResponse.SC_OK);
            }
            
            
        }catch(ParseException | NullPointerException ex){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }catch(SQLException ex){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }   
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        
        //todo
        
    }
    
    
}
