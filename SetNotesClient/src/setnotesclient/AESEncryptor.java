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
class AESEncryptor{
    private static Charset charset = StandardCharsets.ISO_8859_1;
    PaddedBufferedBlockCipher encryptCipher;
    PaddedBufferedBlockCipher decryptCipher;
    SecureRandom rand = new SecureRandom();
    byte[] keyBytes;
    
    
    //debug test
    public static void main(String[] args){
        try{
            AESEncryptor encryptor = new AESEncryptor("PgWN4alJ1XVwN1PdwML0HFPEA6wG8BYb2OF44cC1bek=");
            IvAndCipherText pair = encryptor.encrypt("test 1234567890qwertyuiopasdfghjklzxcvbnm    test 1234567890qwertyuiopasdfghjklzxcvbnm    test 1234567890qwertyuiopasdfghjklzxcvbnm    test 1234567890qwertyuiopasdfghjklzxcvbnm    ");
            
            String text = encryptor.decrypt(pair);
            
            return;
        }catch(UnsupportedEncodingException | InvalidCipherTextException ex){
            Logger.getLogger(AESEncryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    AESEncryptor(String keyStr) throws UnsupportedEncodingException{
        encryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        decryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(keyStr.getBytes(charset));
            keyBytes = md.digest();
        }catch(NoSuchAlgorithmException ex){
            Logger.getLogger(AESEncryptor.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
        
    }
    
    IvAndCipherText encrypt(String payload) throws UnsupportedEncodingException{
        InputStream in = new ByteArrayInputStream(payload.getBytes(charset));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        //prepare encryptCipher
        byte[] ivBytes = new byte[encryptCipher.getBlockSize()];
	    rand.nextBytes(ivBytes);        
        ParametersWithIV params = new ParametersWithIV(new KeyParameter(keyBytes),ivBytes);
        encryptCipher.init(true, params);
        
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
            Logger.getLogger(AESEncryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        encryptCipher.reset();
        
        return new IvAndCipherText(new String(ivBytes, charset), new String(out.toByteArray(), charset));        
    }
    
    //returns decrypted payload
    String decrypt(IvAndCipherText pair) throws UnsupportedEncodingException, InvalidCipherTextException{
        InputStream in = new ByteArrayInputStream(pair.cipherText.getBytes(charset));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        //prepare encryptCipher
        byte[] ivBytes = pair.iv.getBytes(charset);
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
            Logger.getLogger(AESEncryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        decryptCipher.reset();
        
        return new String(out.toByteArray(), charset);     

    }
    
    
    class IvAndCipherText{
        String iv;
        String cipherText;
        IvAndCipherText(String iv, String text){
            this.iv = iv;
            cipherText = text;
        }
    }
}
