/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesclient;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import shared.Util;

/**
 *
 * @author Adam
 */
public class NoteStore{
    private ECPrivateKeyParameters privateKey;
    private final String URL_STR = "http://intentclan.org:8080/SetNotesServer/NotesServlet";
    URL url;
    
    public NoteStore(String privateKeyStr){
        privateKey = Util.base64ToPrivateKey(privateKeyStr);
        try{
            url = new URL("http://intentclan.org:8080/SetNotesServer/NotesServlet");
        }catch(MalformedURLException ex){
            Logger.getLogger(NoteStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String generateKey(){
        ECPrivateKeyParameters key = (ECPrivateKeyParameters)Util.generateKey().getPrivate();
        return Util.privateKeyToBase64(key);
    }
    
    public void addOrUpdate(Note note){
        Thread thread = new Thread(() -> {
            try{
                //todo: localdb
                //send to server
                sendNote(note);
            }catch(IOException ex){
                //todo: add to list of notes to be resent if connection cannot be made, save list locally
            }
        });
        thread.start();
    }
    
    private void sendNote(Note note) throws IOException{
        HttpURLConnection con  = (HttpURLConnection)url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("PUT");
        
        //params
        String noteId = Long.toString(note.getNoteId());
        con.setRequestProperty("noteId", noteId);
        
        //payload
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write("test message");//debug
        //todo: write to stream
        out.close();
        
        ///get response
        int response = con.getResponseCode();
        if(response != HttpURLConnection.HTTP_OK){
            //should be added to list of notes to be resent
            throw new IOException();
        }
    }
    
    public void delete(Note note){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //returns all notes
    public List<Note> getNotes(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
