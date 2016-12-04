/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

/**
 *
 * @author Adam
 */
public class Util{
    
    //debug test main
    public static void main(String[] args){
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
        
        return;
    }
    
    public static ECPublicKeyParameters publicKeyFromPrivate(ECPrivateKeyParameters privateKey){
        //get params
        X9ECParameters curveParams = SECNamedCurves.getByName("secp256r1");
        ECDomainParameters domainParams = new ECDomainParameters(
            curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());
        
        ECPoint q = domainParams.getG().multiply(privateKey.getD());
        
        return new ECPublicKeyParameters(q, domainParams);
    }
    
    public static String privateKeyToBase64(ECPrivateKeyParameters key){
        return Base64.getEncoder().encodeToString(key.getD().toByteArray());
    }
    
    public static String publicKeyToBase64(ECPublicKeyParameters key){
        return Base64.getEncoder().encodeToString(key.getQ().getEncoded(true));
    }
    
    public static ECPrivateKeyParameters base64ToPrivateKey(String keyStr){
        //get params
        X9ECParameters curveParams = SECNamedCurves.getByName("secp256r1");
        ECDomainParameters domainParams = new ECDomainParameters(
            curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());
        
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        
        BigInteger keyInt = new BigInteger(keyBytes);
        return new ECPrivateKeyParameters(keyInt, domainParams);
    }
    
    public static ECPublicKeyParameters base64ToPublicKey(String keyStr){
        //get params
        X9ECParameters curveParams = SECNamedCurves.getByName("secp256r1");
        ECDomainParameters domainParams = new ECDomainParameters(
            curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());
        
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        ECPoint point = domainParams.getCurve().decodePoint(keyBytes);
        
        return new ECPublicKeyParameters(point, domainParams);
    }
    
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
}
