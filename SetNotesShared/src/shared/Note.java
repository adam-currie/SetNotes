/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import java.security.SecureRandom;
import java.util.Date;

/**
 *
 * @author Adam
 */
public class Note{
    private long noteId;
    private Date createDate;
    private Date editDate;
    private String noteBody;

    Note(){
    }
    
    public Note(String noteBody){
        this.noteBody = noteBody;
        noteId = new SecureRandom().nextLong();
        createDate = new Date();
        editDate = (Date)createDate.clone();
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    long getNoteId(){
        return noteId;
    }

    void setNoteId(long noteId){
        this.noteId = noteId;
    }
    
    //gets a copy of the creation date
    public Date getCreateDate(){
        return (Date)createDate.clone();
    }

    void setCreateDate(Date createDate){
        this.createDate = createDate;
    }
    
    //gets a copy of the edit date
    public Date getEditDate(){
        return (Date)editDate.clone();
    }

    void setEditDate(Date editDate){
        this.editDate = editDate;
    }

    public String getNoteBody(){
        return noteBody;
    }

    public void setNoteBody(String noteBody){
        this.noteBody = noteBody;
        editDate = new Date();
    }
    
}
