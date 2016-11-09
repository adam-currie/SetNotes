/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesserver;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Adam
 */
class UserNote{
    private String userId;
    private long noteId;
    private Timestamp createDate;
    private Timestamp editDate;
    private String noteData;
    private boolean isDeleted;
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId) throws NullPointerException {
        if(userId == null){
            throw new NullPointerException();
        }
        this.userId = userId;
    }

    public long getNoteId(){
        return noteId;
    }
    
    public void setNoteId(String noteId) throws NullPointerException, NumberFormatException {
        if(noteId == null){
            throw new NullPointerException();
        }
        this.noteId = Long.parseLong(noteId);
    }

    public Timestamp getCreateDate(){
        return createDate;
    }

    public void setCreateDate(String createDate) throws ParseException {
        this.createDate = new Timestamp(dateFormat.parse(createDate).getTime());
    }

    public Timestamp getEditDate(){
        return editDate;
    }

    public void setEditDate(String editDate) throws ParseException {
        this.editDate = new Timestamp(dateFormat.parse(editDate).getTime());
    }

    public String getNoteBody(){
        return noteData;
    }

    public void setNoteData(String noteBody) throws NullPointerException {
        if(noteBody == null){
            throw new NullPointerException();
        }
        this.noteData = noteBody;
    }

    public boolean isDeleted(){
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) throws NullPointerException {
        switch(isDeleted){
            case "true":
                this.isDeleted = true;
                break;
            case "false":
                this.isDeleted = false;
                break;
            default:
                throw new NullPointerException();
        }
    }
    
    
}
