/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
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
        if(!checkKeyValid(privateKeyStr)){
            throw new IllegalArgumentException("Private key invalid.");
        }

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

    public static boolean checkKeyValid(String key){
        if(key == null || key.isEmpty()){
            return false;
        }

        //todo
        return true;
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

    public void delete(Note note){
        note.setDeleted(true);

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

    //returns all notes
    public void getAllNotes(){
        Thread thread = new Thread(() -> {
            try{
                //todo: use localdb

                //request from server
                sendRequestAllNotes();
            }catch(IOException ex){
            }
        });
        thread.start();
    }

    private void sendNote(Note note) throws IOException{
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        //params
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("noteId", Long.toString(note.getNoteId()));
        params.put("createDate", dateFormat.format(note.getCreateDate()));
        params.put("editDate", dateFormat.format(note.getEditDate()));
        params.put("isDeleted", note.getDeleted());
        params.put("publicKey", Util.publicKeyToBase64(publicKey));

        String cipherText = aes.encrypt(note.getNoteBody());
        params.put("noteData", cipherText);//todo: sign data

        StringBuilder data = new StringBuilder();
        for(Map.Entry<String, Object> param : params.entrySet()){
            if(data.length() != 0){
                data.append('&');
            }
            data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            data.append('=');
            data.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] dataBytes = data.toString().getBytes("UTF-8");
        con.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
        con.getOutputStream().write(dataBytes);

        ///get response
        if(con.getResponseCode() != HttpURLConnection.HTTP_OK){
            //should be added to list of notes to be resent
            throw new IOException();
        }
    }

    private void sendRequestAllNotes() throws IOException{
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        //params
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("publicKey", Util.publicKeyToBase64(publicKey));

        //todo: sign something
        StringBuilder data = new StringBuilder();
        for(Map.Entry<String, Object> param : params.entrySet()){
            if(data.length() != 0){
                data.append('&');
            }
            data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            data.append('=');
            data.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] dataBytes = data.toString().getBytes("UTF-8");
        con.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
        con.getOutputStream().write(dataBytes);

        try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))){
            StringBuilder response = new StringBuilder();

            String line;
            while((line = in.readLine()) != null){
                response.append(line);
            }

            //todo: use event listener to tell ui
            return;//debug
        }
    }

}
