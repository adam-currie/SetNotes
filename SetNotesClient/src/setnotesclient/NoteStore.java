/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesclient;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

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
        con.setRequestProperty("Content-Type", "application/xml");

        
        
        Document doc = null;
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
        }catch(ParserConfigurationException ex){
            Logger.getLogger(NoteStore.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        //add xml elements
        Element root = doc.createElement("note");
        doc.appendChild(root);
        
        Element noteId = doc.createElement("noteId");
        noteId.appendChild(doc.createTextNode(Long.toString(note.getNoteId())));
        root.appendChild(noteId);
        
        Element createDate = doc.createElement("createDate");
        createDate.appendChild(doc.createTextNode(dateFormat.format(note.getCreateDate())));
        root.appendChild(createDate);
        
        Element editDate = doc.createElement("editDate");
        editDate.appendChild(doc.createTextNode(dateFormat.format(note.getEditDate())));
        root.appendChild(editDate);
        
        Element isDeleted = doc.createElement("isDeleted");
        isDeleted.appendChild(doc.createTextNode(String.valueOf(note.getDeleted())));
        root.appendChild(isDeleted);
        
        Element publicKeyNode = doc.createElement("publicKey");
        publicKeyNode.appendChild(doc.createTextNode(Util.publicKeyToBase64(publicKey)));
        root.appendChild(publicKeyNode);
        
        Element noteDataNode = doc.createElement("noteData");
        noteDataNode.appendChild(doc.createTextNode(aes.encrypt(note.getNoteBody())));//todo: sign data
        root.appendChild(noteDataNode);
        
        //get data bytes
        byte[] dataBytes = {};
        try{
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult res = new StreamResult(bos);
            transformer.transform(new DOMSource(doc), res);
            dataBytes = bos.toByteArray();
        }catch(TransformerException ex){
        }
        
        //send data
        con.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
        con.getOutputStream().write(dataBytes);

        ///get response
        if(con.getResponseCode() != HttpURLConnection.HTTP_OK){
            //should be added to list of notes to be resent, probably where this is caught
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
