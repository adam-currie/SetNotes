/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesclient;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Date;

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

    Note(){
    }
    
    public Note(String noteBody){
        this.noteBody = noteBody;
        noteId = new SecureRandom().nextLong();
        createDate = new Timestamp(System.currentTimeMillis());
        editDate = new Timestamp(System.currentTimeMillis());
        isDeleted = false;
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
       this.isDeleted = isDeleted;
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
        this.noteBody = noteBody;
        editDate = new Timestamp(System.currentTimeMillis());
    }
    
}
