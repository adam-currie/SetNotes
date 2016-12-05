/*  
*  File Database.java
*  Project SetNotesServer
*  Authors Adam Currie, Dylan O'Neill, Alexander Martin
*  Date 2016-11-8
*/
package setnotesserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
 * Name     Database
 * Purpose  Data access layer for notes database.
 */
class Database{
    static final String DB_URL = "jdbc:mysql://intentclan.org:3306/notedb";
    static final String USERNAME = "notesadmin";
    static final String PASSWORD = "curriemartinoneill";
    private static boolean driverLoaded = false;
    
    /*
     * Method                   addOrUpdate
     * Description              add or update a note in the database
     * Params           
     *  UserNote note           the note to add or update
     */
    static void addOrUpdate(UserNote note) throws SQLException{
        if(driverLoaded == false){
            loadDriver();
        }
        
        try(Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            
            //todo: stop if this note's edit date is before a note with the same id on the db
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO note (noteid,userid,creation,lastedited,notebody,deleted,signature) VALUES (?, ?, ?, ?, ?, ?, ?) " + 
                    "ON DUPLICATE KEY UPDATE lastedited=VALUES(lastedited), notebody=VALUES(notebody), deleted=VALUES(deleted), signature=VALUES(signature)");
            
            statement.setLong(1, note.getNoteId());
            statement.setString(2, note.getUserId());
            statement.setTimestamp(3, note.getCreateDate());
            statement.setTimestamp(4, note.getEditDate());
            statement.setString(5, note.getNoteBody());
            statement.setBoolean(6, false);
            statement.setString(7, note.getSignature());
            
            statement.execute();
            
        }catch(SQLException ex) {
            throw ex;
        }
    }
    
    /*
     * Method                       delete
     * Description                  deletes a note in the database
     * Params           
     *  String userId               the user id of the note to delete
     *  String noteId               the note id of the note to delete
     *  String Signature            the note user signature
     */
    static void delete(String userId, long noteId, String signature) throws SQLException{
        if(driverLoaded == false){
            loadDriver();
        }
        
        try(Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
                        
            //todo: stop if this note's edit date is before a note with the same id on the db
            
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE note SET deleted=TRUE, notebody=NULL, signature=? WHERE userid=? AND noteid=?");
            
            statement.setString(1, signature);
            statement.setString(2, userId);
            statement.setLong(3, noteId);
            
            statement.execute();
            
        }catch(SQLException ex) {
            throw ex;
        }
    }
    
    /*
     * Method                       loadDriver
     * Description                  load the jdbc driver
     */
    private static void loadDriver(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", ex);
        }
    }
    
    /*
     * Method                   getAllNotes
     * Description              get all notes for a user
     * Params           
     *  String userId           the id of the user to get notes for
     * Returns          
     *  ArrayList<UserNote>     the notes
     */
    static ArrayList<UserNote> getAllNotes(String userId) throws SQLException{
        if(driverLoaded == false){
            loadDriver();
        }
        
        try(Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
                        
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM note WHERE userid=?");
            statement.setString(1, userId);
            ResultSet results = statement.executeQuery();
            
            ArrayList<UserNote> notes = new ArrayList();
            while(results.next()){
                UserNote note = new UserNote();
                
                note.setNoteId(results.getLong("noteid"));
                note.setUserId(results.getString("userid"));
                note.setCreateDate(results.getTimestamp("creation"));
                note.setEditDate(results.getTimestamp("lastedited"));
                note.setNoteBody(results.getString("notebody"));
                note.setIsDeleted(results.getBoolean("deleted"));
                note.setSignature(results.getString("signature"));
                
                notes.add(note);
            }
            
            return notes;
            
        }catch(SQLException ex) {
            throw ex;
        }
    }
    
}

