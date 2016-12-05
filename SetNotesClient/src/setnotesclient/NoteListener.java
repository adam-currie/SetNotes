/*  
*  File NoteListener.java
*  Project SetNotesClient
*  Authors Adam Currie, Dylan O'Neill
*  Date 2016-11-8
*/
package setnotesclient;

import java.util.ArrayList;

/*
 * Name     NoteListener
 * Purpose  Listens for added notes.
 */
public interface NoteListener{
    /*
     * Method           notesAdded
     * Description      called when notes are being added
     * Params           
     *  ArrayList<Note> notes   the notes to be added
     */
    public void notesAdded(ArrayList<Note> notes);
}
