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
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import shared.Util;

/**
 *
 * @author Adam
 */
public class NoteStore{
    PaddedBufferedBlockCipher encryptCipher;
    PaddedBufferedBlockCipher decryptCipher;    
    private ECPrivateKeyParameters privateKey;
    private ECPublicKeyParameters publicKey;
    private final String URL_STR = "http://intentclan.org:8080/SetNotesServer/NotesServlet";
    URL url;
    
    public NoteStore(String privateKeyStr){
        privateKey = Util.base64ToPrivateKey(privateKeyStr);
        publicKey = Util.publicKeyFromPrivate(privateKey);
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
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");        
        
        //params
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("noteId", "debug& p = %&d="+Long.toString(note.getNoteId()));
        params.put("createDate", note.getCreateDate());
        params.put("editDate", note.getEditDate());
        params.put("isDeleted", note.getDeleted());
        params.put("publicKey", Util.publicKeyToBase64(publicKey));
        //todo params.put("noteData", encrypt(note.getNoteBody()));//todo, sign
        
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        con.getOutputStream().write(postDataBytes);
        
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
