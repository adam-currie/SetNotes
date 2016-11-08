/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 *
 * @author Adam
 */
class AESEncryption{
    private Charset charset = StandardCharsets.ISO_8859_1;
    PaddedBufferedBlockCipher encryptCipher;
    PaddedBufferedBlockCipher decryptCipher;
    SecureRandom rand = new SecureRandom();
    byte[] keyBytes;
    
    
    //debug
    public static void main(String[] args) throws UnsupportedEncodingException, InvalidCipherTextException{
        AESEncryption aes = new AESEncryption("testkeystring");
        String cipherText = aes.encrypt(" test string test string test string test string test string test string test string test string test string test string test string test string test string test string test string test string test string test string");
        
        String secret = aes.decrypt(cipherText);
        
        return;
    }

    AESEncryption(String keyStr) {
        encryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        decryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(keyStr.getBytes(charset));
            keyBytes = md.digest();
        }catch(NoSuchAlgorithmException ex){
            Logger.getLogger(AESEncryption.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
        
    }
    
    String encrypt(String payload) throws UnsupportedEncodingException{
        InputStream in = new ByteArrayInputStream(payload.getBytes(charset));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        //prepare encryptCipher
        byte[] ivBytes = new byte[encryptCipher.getBlockSize()];
	    rand.nextBytes(ivBytes);        
        ParametersWithIV params = new ParametersWithIV(new KeyParameter(keyBytes),ivBytes);
        encryptCipher.init(true, params);
        
        //add iv to cipherText
        out.write(ivBytes, 0, ivBytes.length);
        
        //process
        int numBytesRead;        //number of bytes read from input
        int numBytesProcessed;   //number of bytes processed
        byte[] buf = new byte[16];
        byte[] obuf = new byte[16];
        try{
            while ((numBytesRead = in.read(buf)) >= 0) {
                numBytesProcessed = encryptCipher.processBytes(buf, 0, numBytesRead, obuf, 0);
                out.write(obuf, 0, numBytesProcessed);
            }
        
            numBytesProcessed = encryptCipher.doFinal(obuf, 0);
            out.write(obuf, 0, numBytesProcessed);
            out.flush();
            in.close();
            out.close();
        }catch(IOException | DataLengthException | IllegalStateException | InvalidCipherTextException ex){
            Logger.getLogger(AESEncryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        encryptCipher.reset();
        
        return Base64.getEncoder().encodeToString(out.toByteArray());      
    }
    
    //returns decrypted payload
    String decrypt(String cipherText) throws UnsupportedEncodingException, InvalidCipherTextException{
        InputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(cipherText));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        //prepare encryptCipher
        byte[] ivBytes = new byte[decryptCipher.getBlockSize()];
        try{
            in.read(ivBytes, 0, ivBytes.length);
        }catch(IOException ex){
            Logger.getLogger(AESEncryption.class.getName()).log(Level.SEVERE, null, ex);
            throw new InvalidCipherTextException();
        }
        ParametersWithIV params = new ParametersWithIV(new KeyParameter(keyBytes),ivBytes);
        decryptCipher.init(false, params);
        
        //process
        int numBytesRead;
        int numBytesProcessed;
        byte[] buf = new byte[16];
        byte[] obuf = new byte[16];
        try{
            while ((numBytesRead = in.read(buf)) >= 0) {
                numBytesProcessed = decryptCipher.processBytes(buf, 0, numBytesRead, obuf, 0);
                out.write(obuf, 0, numBytesProcessed);
            }
        
            numBytesProcessed = decryptCipher.doFinal(obuf, 0);
            out.write(obuf, 0, numBytesProcessed);
            out.flush();
            in.close();
            out.close();
        }catch(IOException | DataLengthException | IllegalStateException | InvalidCipherTextException ex){
            Logger.getLogger(AESEncryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        decryptCipher.reset();
        
        return new String(out.toByteArray(), charset);
    }

}
