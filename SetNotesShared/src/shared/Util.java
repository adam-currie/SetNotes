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
    
    //debug main
    public static void main(String[] args){
        //todo: test these methods
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
    
    public static ECPrivateKeyParameters generateKey(){
        //get params
        X9ECParameters curveParams = SECNamedCurves.getByName("secp256r1");
        ECDomainParameters domainParams = new ECDomainParameters(
            curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());
        
        //generate key
        ECKeyGenerationParameters keyGenParams = new ECKeyGenerationParameters(domainParams, new SecureRandom());
        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        generator.init(keyGenParams);
        ECPrivateKeyParameters key = (ECPrivateKeyParameters) generator.generateKeyPair().getPrivate();

        return key;
    }
}
