/*  
*  File ECDSAUtil.java
*  Project SetNotesShared
*  Authors Adam Currie, Dylan O'Neill, Alexander Martin
*  Date 2016-11-8
*/
package shared;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.Deflater;
import static java.util.zip.Deflater.FILTERED;
import static java.util.zip.Deflater.HUFFMAN_ONLY;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.math.ec.ECPoint;

/*
 * Name     ECDSAUtil
 * Purpose  Utility class for ECDSA keys and signing.
 */
public class ECDSAUtil{
    
    /*
     * Method           main
     * Description      test main  
     */
    public static void main(String[] args) throws InvalidKeyException{
        AsymmetricCipherKeyPair pair = generateKey();
        ECPrivateKeyParameters priv = (ECPrivateKeyParameters)pair.getPrivate();
        ECPublicKeyParameters pub = (ECPublicKeyParameters)pair.getPublic();
        System.out.println("private key: " + privateKeyToBase64(priv));
        System.out.println("Public key:  " + publicKeyToBase64(pub));
        
        System.out.println("public key from private: " + publicKeyToBase64(publicKeyFromPrivate(priv)));
        
        System.out.println("public key to base64, back to key, and back to base64: " + 
                publicKeyToBase64(base64ToPublicKey(publicKeyToBase64(pub)))
        );
        
        System.out.println("private key to base64, back to key, and back to base64: " + 
                privateKeyToBase64(base64ToPrivateKey(privateKeyToBase64(priv)))
        );
        
        ECPublicKeyParameters key = ECDSAUtil.base64ToPublicKey("56456456");
        
        return;
    }
    
    /*
     * Method                               publicKeyFromPrivate
     * Description                          get a public key from a private key
     * Params           
     *  ECPrivateKeyParameters privateKey   the private key
     * Returns          
     *  ECPublicKeyParameters               the public key
     */
    public static ECPublicKeyParameters publicKeyFromPrivate(ECPrivateKeyParameters privateKey){
        //get params
        X9ECParameters curveParams = SECNamedCurves.getByName("secp256r1");
        ECDomainParameters domainParams = new ECDomainParameters(
            curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());
        
        ECPoint q = domainParams.getG().multiply(privateKey.getD());
        
        return new ECPublicKeyParameters(q, domainParams);
    }
    
    /*
     * Method                               privateKeyToBase64
     * Description                          get a base64 string from a private key
     * Params           
     *  ECPrivateKeyParameters privateKey   the private key
     * Returns          
     *  String                              the key string
     */
    public static String privateKeyToBase64(ECPrivateKeyParameters key){
        //todo: maybe compress this        
        return Base64.getEncoder().encodeToString(key.getD().toByteArray());
    }
    
    /*
     * Method                               publicKeyToBase64
     * Description                          get a base64 string from a public key
     * Params           
     *  ECPublicKeyParameters privateKey    the public key
     * Returns          
     *  String                              the key string
     */
    public static String publicKeyToBase64(ECPublicKeyParameters key){
        return Base64.getEncoder().encodeToString(key.getQ().getEncoded(true));
    }
    
    /*
     * Method                   base64ToPrivateKey
     * Description              get a base64 string from a privateKey
     * Params           
     *  String keyStr           the key string
     * Returns          
     *  ECPrivateKeyParameters  the converted key
     */
    public static ECPrivateKeyParameters base64ToPrivateKey(String keyStr) throws InvalidKeyException{
        try{
            //get params
            X9ECParameters curveParams = SECNamedCurves.getByName("secp256r1");
            ECDomainParameters domainParams = new ECDomainParameters(
                curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());

            byte[] keyBytes = Base64.getDecoder().decode(keyStr);

            BigInteger keyInt = new BigInteger(keyBytes);
            return new ECPrivateKeyParameters(keyInt, domainParams);
        }catch(ArrayIndexOutOfBoundsException | IllegalArgumentException ex){
            throw new InvalidKeyException("Invalid key length.");
        }
    }
    
    /*
     * Method                   base64ToPublicKey
     * Description              get a base64 string from a public key
     * Params           
     *  String keyStr           the key string
     * Returns          
     *  ECPublicKeyParameters   the converted key
     */
    public static ECPublicKeyParameters base64ToPublicKey(String keyStr) throws InvalidKeyException{
        try{
            //get params
            X9ECParameters curveParams = SECNamedCurves.getByName("secp256r1");
            ECDomainParameters domainParams = new ECDomainParameters(
                curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());

            byte[] keyBytes = Base64.getDecoder().decode(keyStr);
            ECPoint point = domainParams.getCurve().decodePoint(keyBytes);

            return new ECPublicKeyParameters(point, domainParams);
        }catch(ArrayIndexOutOfBoundsException | IllegalArgumentException ex){
            throw new InvalidKeyException("Invalid key length.");
        }
    }
    
    /*
     * Method                   generateKey
     * Description              generate a key pair
     * Returns          
     *  AsymmetricCipherKeyPair the generated key pair
     */
    public static AsymmetricCipherKeyPair generateKey(){
        //get params
        X9ECParameters curveParams = SECNamedCurves.getByName("secp256r1");
        ECDomainParameters domainParams = new ECDomainParameters(
            curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());
        
        //generate key
        ECKeyGenerationParameters keyGenParams = new ECKeyGenerationParameters(domainParams, new SecureRandom());
        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        generator.init(keyGenParams);

        return generator.generateKeyPair();
    }
    
    /*
     * Method                       signStr
     * Description                  sign the string with the specified key
     * Params           
     *  ECPrivateKeyParameters key  private key to sign with
     *  String message              the message to sign
     * Returns          
     *  String                      signature string
     */
    public static String signStr(ECPrivateKeyParameters key, String message){
        byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
        
        ECDSASigner signer = new ECDSASigner();
        signer.init(true, key);
        BigInteger[] rs = signer.generateSignature(msgBytes);
        
        String sigStr = Base64.getEncoder().encodeToString(rs[0].toByteArray()) + 
                        Base64.getEncoder().encodeToString(rs[1].toByteArray());

        return sigStr;
    }
    
    /*
     * Method                       checkSignature
     * Description                  verify that the signature matches the message
     * Params           
     *  String message              the signed message
     *  String signature            the signature to check
     * Returns          
     *  boolean                     whether the signature matched
     */
    public static boolean checkSignature(ECPublicKeyParameters key, String message, String signature){
        byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
        
        ECDSASigner signer = new ECDSASigner();
        signer.init(false, key);
        
        byte[] rBytes = Base64.getDecoder().decode(signature.substring(0, 44));
        byte[] sBytes = Base64.getDecoder().decode(signature.substring(44, 88));
        
        return signer.verifySignature(msgBytes, new BigInteger(rBytes), new BigInteger(sBytes));
    }
    
}
