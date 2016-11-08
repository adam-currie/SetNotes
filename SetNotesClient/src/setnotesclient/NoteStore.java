/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesclient;

import java.util.List;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import shared.Util;

/**
 *
 * @author Adam
 */
public class NoteStore{
    private ECPrivateKeyParameters privateKey;
    
    public NoteStore(String privateKeyStr){
        privateKey = Util.base64ToPrivateKey(privateKeyStr);
    }
    
    public static String generateKey(){
        ECPrivateKeyParameters key = (ECPrivateKeyParameters)Util.generateKey().getPrivate();
        return Util.privateKeyToBase64(key);
    }
    
    public void addOrUpdate(Note note){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void delete(Note note){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //returns all notes
    public List<Note> getNotes(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
