/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Adam
 */
class DatabaseManager{
static final String DB_URL = "jdbc:mysql://intentclan.org:3306/notedb";
static final String USERNAME = "notesadmin";
static final String PASSWORD = "curriemartinoneill";
    

    public static void main(String[] args){
        System.out.println("Connecting database...");

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            System.out.println("Database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    
    DatabaseManager(){
        //todo
    }
    
}

