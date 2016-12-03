/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Adam
 */
class Database{
    static final String DB_URL = "jdbc:mysql://intentclan.org:3306/notedb";
    static final String USERNAME = "notesadmin";
    static final String PASSWORD = "curriemartinoneill";
    private static boolean driverLoaded = false;
    

    static void addOrUpdate(UserNote note) throws SQLException{
        if(driverLoaded == false){
            loadDriver();
        }
        
        try(Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            
            
            //todo: stop if this note's edit date is before a note with the same id on the db
            //todo: update instead of insert if note already exists
            
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO note (noteid,userid,creation,lastedited,notebody,deleted) VALUES (?, ?, ?, ?, ?, ?) " + 
                    "ON DUPLICATE KEY UPDATE lastedited=VALUES(lastedited), notebody=VALUES(notebody), deleted=VALUES(deleted)");
            
            statement.setLong(1, note.getNoteId());
            statement.setString(2, note.getUserId());
            statement.setTimestamp(3, note.getCreateDate());
            statement.setTimestamp(4, note.getEditDate());
            statement.setString(5, note.getNoteBody());
            statement.setBoolean(6, false);
            
            statement.execute();
            
        }catch(SQLException ex) {
            throw ex;
        }
    }

    static void delete(String userId, long noteId) throws SQLException{
        if(driverLoaded == false){
            loadDriver();
        }
        
        try(Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            
            
            //todo: stop if this note's edit date is before a note with the same id on the db
            
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE note SET deleted=TRUE, notebody=NULL WHERE userid=? AND noteid=?");
            
            statement.setString(1, userId);
            statement.setLong(2, noteId);
            
            statement.execute();
            
        }catch(SQLException ex) {
            throw ex;
        }
    }

    private static void loadDriver(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }
    }
    
}

