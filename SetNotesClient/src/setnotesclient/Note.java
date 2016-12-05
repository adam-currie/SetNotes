/*  
*  File Note.java
*  Project SetNotesClient
*  Authors Adam Currie, Dylan O'Neill, Alexander Martin
*  Date 2016-11-8
*/
package setnotesclient;

import java.security.SecureRandom;
import java.sql.Timestamp;

/*
 * Name     Note
 * Purpose  Storage class for a note.
 */
public class Note{
    private long noteId;
    private Timestamp createDate;
    private Timestamp editDate;
    private String noteBody;
    private boolean isDeleted;
    
    /*
     * Method           createNote
     * Description      create and initialize a note
     * Returns
     *  Note            created note
     */
    public static Note createNote(){
        Note note = new Note();
        
        note.noteBody = "";
        note.noteId = new SecureRandom().nextLong();
        note.createDate = new Timestamp(System.currentTimeMillis());
        note.editDate = new Timestamp(System.currentTimeMillis());
        note.isDeleted = false;
        
        return note;
    }
    
    /*
     * Method           getCreateDate
     * Description      gets a copy of the creation date
     * Returns
     *  Timestamp       creation date/time
     */
    public Timestamp getCreateDate(){
        return (Timestamp)createDate.clone();
    }
        
    /*
     * Method           getEditDate
     * Description      gets a copy of the edit date
     * Returns
     *  Timestamp       edit date/time
     */
    public Timestamp getEditDate(){
        return (Timestamp)editDate.clone();
    }
    
    /*
     * Method           getNoteBody
     * Description      get the note body
     * Returns
     *  String          note body
     */
    public String getNoteBody(){
        return noteBody;
    }
    
    /*
     * Method           setNoteBody
     * Description      set the note body
     * Params           
     *  String paload   new note body
     */
    public void setNoteBody(String noteBody){
        if(this.noteBody != null && this.noteBody.equals(noteBody)){
            return;
        }
        
        this.noteBody = noteBody;
        editDate = new Timestamp(System.currentTimeMillis());
    }
    
    /*
     * Method           Note
     * Description      hide default constructor from public api
     * Returns
     *  Note            new note
     */
    Note(){
    }
    
    /*
     * Method           getNoteId
     * Description      get the note id
     * Returns
     *  Long            note id
     */
    long getNoteId(){
        return noteId;
    }
    
    /*
     * Method           setNoteId
     * Description      set the note id
     * Params
     *  Long noteId     the new id of the note
     */
    void setNoteId(long noteId){
        this.noteId = noteId;
    }
    
    /*
     * Method                   setCreateDate
     * Description              set the creation date of the note
     * Params
     *  Timestamp createDate    the new creation date of the note
     */
    void setCreateDate(Timestamp createDate){
        this.createDate = createDate;
    }
    
    /*
     * Method               setDeleted
     * Description          set the deleted status
     * Params
     *  Boolean isDeleted   the new deleted status
     */
    void setDeleted(Boolean isDeleted){
        if(this.isDeleted == isDeleted){
            return;
        }
        
        this.isDeleted = isDeleted;
        if(isDeleted){
            noteBody = "deleted";
        }
    }
    
    /*
     * Method               getDeleted
     * Description          get the deleted status
     * Returns
     *  Boolean             the deleted status of the note
     */
    boolean getDeleted(){
        return isDeleted;
    }
    
    /*
     * Method               setEditDate
     * Description          set the edited date/time
     * Params
     *  Timestamp editDate  the new edit date
     */
    void setEditDate(Timestamp editDate){
        this.editDate = editDate;
    }

}
