/*  
*  File NoteStore.java
*  Project SetNotesClient
*  Authors Adam Currie, Dylan O'Neill
*  Date 2016-11-8
*/
package setnotesclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import shared.ECDSAUtil;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * Name     NoteStore
 * Purpose  Client front-end for storing and retrieving notes.
 */
public class NoteStore{

    AESEncryption aes;
    PaddedBufferedBlockCipher encryptCipher;
    PaddedBufferedBlockCipher decryptCipher;
    private ECPrivateKeyParameters privateKey;
    private ECPublicKeyParameters publicKey;
    private final String URL_STR = "http://intentclan.org:8080/SetNotesServer/NotesServlet";
    private URL url;
    private NoteListener listener;

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /*
     * Method                       NoteStore
     * Description                  constructs a notestore with a key string and listener
     * Params           
     *  String privateKeyStr        private key in base64
     *  NoteListener noteListener   listener for when notes are added, called from another thread
     * Returns
     *  NoteStore                   new NoteStore
     */
    public NoteStore(String privateKeyStr, NoteListener noteListener){
        if(!checkKeyValid(privateKeyStr)){
            throw new IllegalArgumentException("Private key invalid.");
        }
        
        listener = noteListener;

        try{
            privateKey = ECDSAUtil.base64ToPrivateKey(privateKeyStr);
        }catch(InvalidKeyException ex){
            throw new IllegalArgumentException("Private key invalid.");
        }
        
        publicKey = ECDSAUtil.publicKeyFromPrivate(privateKey);
        
        try{
            url = new URL(URL_STR);
        }catch(MalformedURLException ex){
            Logger.getLogger(NoteStore.class.getName()).log(Level.SEVERE, null, ex);
        }

        //todo: possibly use seperate key
        aes = new AESEncryption(privateKeyStr);
    }
    
    /*
     * Method               generateKey
     * Description          generates a private key in base64
     * Returns
     *  String              generated key
     */
    public static String generateKey(){
        ECPrivateKeyParameters key = (ECPrivateKeyParameters)ECDSAUtil.generateKey().getPrivate();
        return ECDSAUtil.privateKeyToBase64(key);
    }
    
    /*
     * Method               checkKeyValid
     * Description          checks the validity of a key
     * Params           
     *  String key          private key in base 64
     * Returns          
     *  Boolean             whether the key is a valid ecdsa key
     */
    public static boolean checkKeyValid(String key){
        if(key == null || key.isEmpty()){
            return false;
        }

        //todo: check if this is a valid ecdsakey
        return true;
    }
    
    /*
     * Method               addOrUpdate
     * Description          adds or updates a note
     * Params           
     *  Note note           note to add or update
     */
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
    
    /*
     * Method               delete
     * Description          deletes a note
     * Params           
     *  Note note           the note to be removed
     */
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

    /*
     * Method           getAllNotes
     * Description      sends a request to get all notes, 
     *                  returned via noteListener on a seperate thread
     */
    public void getAllNotes(){
        Thread thread = new Thread(() -> {
            try{
                //todo: use localdb

                //request from server
                sendRequestAllNotes();
            }catch(IOException ex){
                //todo: check again later
                Logger.getLogger(NoteStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        thread.start();
    }
    
    /*
     * Method               sendNote
     * Description          sends a note to the server
     * Params           
     *  Note note           the note to send
     */
    private void sendNote(Note note) throws IOException{
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/xml");

        Document doc = null;
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            doc = builder.newDocument();
        }catch(ParserConfigurationException ex){
            Logger.getLogger(NoteStore.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        //NOTE ROOT
        Element root = doc.createElement("noteRoot");
            //NOTE
            Element noteNode = doc.createElement("note");
                Element noteId = doc.createElement("noteId");
                noteId.appendChild(doc.createTextNode(Long.toString(note.getNoteId())));
                noteNode.appendChild(noteId);

                Element createDate = doc.createElement("createDate");
                createDate.appendChild(doc.createTextNode(dateFormat.format(note.getCreateDate())));
                noteNode.appendChild(createDate);

                Element editDate = doc.createElement("editDate");
                editDate.appendChild(doc.createTextNode(dateFormat.format(note.getEditDate())));
                noteNode.appendChild(editDate);

                Element isDeleted = doc.createElement("isDeleted");
                isDeleted.appendChild(doc.createTextNode(String.valueOf(note.getDeleted())));
                noteNode.appendChild(isDeleted);

                Element publicKeyNode = doc.createElement("publicKey");
                String keyStr = ECDSAUtil.publicKeyToBase64(publicKey);
                publicKeyNode.appendChild(doc.createTextNode(keyStr));
                noteNode.appendChild(publicKeyNode);

                Element noteDataNode = doc.createElement("noteData");
                noteDataNode.appendChild(doc.createTextNode(aes.encrypt(note.getNoteBody())));
                noteNode.appendChild(noteDataNode);
            root.appendChild(noteNode);
            
            //SIGNATURE
            Element signatureNode = doc.createElement("signature");
                signatureNode.appendChild(doc.createTextNode(ECDSAUtil.signStr(privateKey, noteNode.getTextContent())));
            root.appendChild(signatureNode);
        doc.appendChild(root);
        
        //get data bytes
        byte[] dataBytes = {};
        try{
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);
            transformer.transform(new DOMSource(doc), result);
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
    
    /*
     * Method               sendRequestAllNotes
     * Description          sends a request for all notes
     */
    private void sendRequestAllNotes() throws IOException{
        String queryStr = String.format("?publicKey=%s",
                URLEncoder.encode(ECDSAUtil.publicKeyToBase64(publicKey), "UTF-8"));
        
        URL getUrl = new URL(URL_STR+queryStr);
        HttpURLConnection con = (HttpURLConnection)getUrl.openConnection();
        con.setRequestMethod("GET");

        if(con.getResponseCode() != HttpURLConnection.HTTP_OK){
            throw new IOException("response code: " + con.getResponseCode());
        }
        
        //build document
        Document doc = null;
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            doc = builder.parse(con.getInputStream());
        }catch(ParserConfigurationException ex){
            Logger.getLogger(NoteStore.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }catch(SAXException ex){
            throw new IOException(ex);
        }
        
        //get list of notes
        ArrayList<Note> notes = new ArrayList();//todo: local db update
        ArrayList<Note> deletedNotes = new ArrayList();
        try {
            Element noteListRootNode = doc.getDocumentElement();

            NodeList rootList = noteListRootNode.getElementsByTagName("noteRoot");
            for(int i=0; i<rootList.getLength(); i++){
                Element noteRootNode = (Element)rootList.item(i);
                    Element noteNode = (Element)noteRootNode.getElementsByTagName("note").item(0);
                        Node createNode = noteNode.getElementsByTagName("createDate").item(0);
                        Node editNode = noteNode.getElementsByTagName("editDate").item(0);
                        Node idNode = noteNode.getElementsByTagName("noteId").item(0);
                        Node deletedNode = noteNode.getElementsByTagName("isDeleted").item(0);
                        Node noteDataNode = noteNode.getElementsByTagName("noteData").item(0);
                    Element signatureNode = (Element)noteRootNode.getElementsByTagName("signature").item(0);

                Note note = new Note();
                note.setCreateDate(new Timestamp(dateFormat.parse(createNode.getTextContent()).getTime()));
                note.setEditDate(new Timestamp(dateFormat.parse(editNode.getTextContent()).getTime()));
                note.setNoteId(Long.parseLong(idNode.getTextContent()));
                note.setDeleted(Boolean.parseBoolean(deletedNode.getTextContent()));
                
                //check signature
                if(ECDSAUtil.checkSignature(publicKey, noteNode.getTextContent(), signatureNode.getTextContent())){
                    try{
                        note.setNoteBody(aes.decrypt(noteDataNode.getTextContent()));
                    }catch(NullPointerException ex){
                        note.setNoteBody(null);
                    }

                    if(note.getDeleted()){
                        deletedNotes.add(note);
                    }else{
                        notes.add(note);
                    }
                }
            }
        }catch(ParseException | UnsupportedEncodingException | InvalidCipherTextException ex){
            throw new IOException(ex);
        }

        //pass notes back to listener
        listener.notesAdded(notes);
        
    }
    
    /*
     * Method               getPrivateKey
     * Description          gets the private key associated with this NoteStore
     * Returns
     *  String              the private key in base64
     */
    public String getPrivateKey(){
        return ECDSAUtil.privateKeyToBase64(privateKey);
    }

}
