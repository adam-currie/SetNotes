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
import java.text.SimpleDateFormat;
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
    AESEncryption aes;
    PaddedBufferedBlockCipher encryptCipher;
    PaddedBufferedBlockCipher decryptCipher;    
    private ECPrivateKeyParameters privateKey;
    private ECPublicKeyParameters publicKey;
    private final String URL_STR = "http://intentclan.org:8080/SetNotesServer/NotesServlet";
    URL url;
    
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public NoteStore(String privateKeyStr){
        privateKey = Util.base64ToPrivateKey(privateKeyStr);
        publicKey = Util.publicKeyFromPrivate(privateKey);
        String debug = Util.publicKeyToBase64(publicKey);
        try{
            url = new URL("http://intentclan.org:8080/SetNotesServer/NotesServlet");
        }catch(MalformedURLException ex){
            Logger.getLogger(NoteStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //todo: possibly use seperate key
        aes = new AESEncryption(privateKeyStr);
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
                //todo: add to list of notes to be resent if connection cannot be made
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
        params.put("noteId", Long.toString(note.getNoteId()));
        params.put("createDate", dateFormat.format(note.getCreateDate()));
        params.put("editDate", dateFormat.format(note.getEditDate()));
        params.put("isDeleted", note.getDeleted());
        params.put("publicKey", Util.publicKeyToBase64(publicKey));
        
        String cipherText = aes.encrypt(note.getNoteBody());
        params.put("noteData", cipherText);//todo: sign data
        
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
        if(con.getResponseCode() != HttpURLConnection.HTTP_OK){
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
