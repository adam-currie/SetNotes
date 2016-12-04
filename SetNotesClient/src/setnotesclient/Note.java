/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesclient;

import java.awt.PageAttributes;
import java.security.SecureRandom;
import java.sql.Timestamp;

/**
 *
 * @author Adam
 */
public class Note{
    private long noteId;
    private Timestamp createDate;
    private Timestamp editDate;
    private String noteBody;
    private boolean isDeleted;
    
    //create and initialize note
    public static Note createNote(){
        Note note = new Note();
        
        note.noteBody = "";
        note.noteId = new SecureRandom().nextLong();
        note.createDate = new Timestamp(System.currentTimeMillis());
        note.editDate = new Timestamp(System.currentTimeMillis());
        note.isDeleted = false;
        
        return note;
    }

    public Note(long noteId, Timestamp createDate, Timestamp editDate, String noteBody, boolean isDeleted){
        this.noteId = noteId;
        this.createDate = createDate;
        this.editDate = editDate;
        this.noteBody = noteBody;
        this.isDeleted = isDeleted;
    }
    
    //hidden from public
    Note(){
    }
    
    long getNoteId(){
        return noteId;
    }

    void setNoteId(long noteId){
        this.noteId = noteId;
    }
    
    //gets a copy of the creation date
    public Timestamp getCreateDate(){
        return (Timestamp)createDate.clone();
    }

    void setCreateDate(Timestamp createDate){
        this.createDate = createDate;
    }
    
    void setDeleted(Boolean isDeleted){
        if(this.isDeleted == isDeleted){
            return;
        }
        
        this.isDeleted = isDeleted;
        if(isDeleted){
            noteBody = "deleted";
        }
    }
    
    boolean getDeleted(){
        return isDeleted;
    }
    
    //gets a copy of the edit date
    public Timestamp getEditDate(){
        return (Timestamp)editDate.clone();
    }

    void setEditDate(Timestamp editDate){
        this.editDate = editDate;
    }

    public String getNoteBody(){
        return noteBody;
    }

    public void setNoteBody(String noteBody){
        if(this.noteBody != null && this.noteBody.equals(noteBody)){
            return;
        }
        
        this.noteBody = noteBody;
        editDate = new Timestamp(System.currentTimeMillis());
    }
    
}
