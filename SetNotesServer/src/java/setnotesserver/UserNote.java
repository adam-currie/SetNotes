/*  
*  File UserNote.java
*  Project SetNotesServer
*  Authors Adam Currie, Dylan O'Neill, Alexander Martin
*  Date 2016-11-8
*/
package setnotesserver;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
 * Name     UserNote
 * Purpose  Stores information about a note associated with a user.
 */
class UserNote{
    private String userId;
    private long noteId;
    private Timestamp createDate;
    private Timestamp editDate;
    private String noteData;
    private String signature;
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
        setNoteId(Long.parseLong(noteId));
    }
    
    public void setNoteId(long noteId){
        this.noteId = noteId;
    }

    public Timestamp getCreateDate(){
        return createDate;
    }
    
    public String getCreateDateString(){
        return dateFormat.format(getCreateDate());
    }

    public void setCreateDate(String createDate) throws ParseException {
        setCreateDate(new Timestamp(dateFormat.parse(createDate).getTime()));
    }

    public void setCreateDate(Timestamp createDate){
        this.createDate = createDate;
    }

    public Timestamp getEditDate(){
        return editDate;
    }
    
    public String getEditDateString(){
        return dateFormat.format(getEditDate());
    }

    public void setEditDate(String editDate) throws ParseException {
        setEditDate(new Timestamp(dateFormat.parse(editDate).getTime()));
    }
    
    public void setEditDate(Timestamp editDate){
        this.editDate = editDate;
    }

    public String getNoteBody(){
        return noteData;
    }

    public void setNoteBody(String noteBody) {
        this.noteData = noteBody;
    }
    
    public String getSignature(){
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isDeleted(){
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) throws NullPointerException {
        switch(isDeleted){
            case "true":
                setIsDeleted(true);
                break;
            case "false":
                setIsDeleted(false);
                break;
            default:
                throw new NullPointerException();
        }
    }
    
    public void setIsDeleted(boolean isDeleted){
        this.isDeleted = isDeleted;
    }
    
    
}
