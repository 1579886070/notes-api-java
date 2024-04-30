package com.zxxwl.common.crypto;



import com.zxxwl.common.utils.Constants;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

public class RSA {

    private RSAPrivateKey PrivateKey = null;
    private RSAPublicKey PublicKey  = null;

    public static class Buffer{
        public byte[] result;
        public Buffer(byte[] result){
            this.result = result;
        }

        public String toBase64(){
            return Base64.getEncoder().encodeToString(this.result);
        }

        public String toHex(){
            return XOR.bin2hex(result);
        }

        public static byte[] FromBase64(String data){
            return Base64.getDecoder().decode(data);
        }

        public static byte[] FromHex(String data){
            return XOR.hex2bin(data);
        }
    }

    public RSA(String dir){
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Can not get the rsa key factory");
        }

        byte[] bytes = null;
        if( factory != null ){
            try {
                this.PublicKey = (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(bytes));
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }

            try {
                this.PrivateKey = (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(bytes));
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
    }

    public Buffer encrypt(String data, boolean usePublic){
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, usePublic ? PublicKey : PrivateKey);
        if( cipher == null )
            return null;

        try {
            byte[] result = cipher.doFinal(data.getBytes(Constants.DefaultCharset));
            return new Buffer(result);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }

    public String decrypt(byte[] data, boolean usePublic){
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, usePublic ? PublicKey : PrivateKey);
        if( cipher == null )
            return null;

        try {
            byte[] result = cipher.doFinal(data);
            return new String(result, Constants.DefaultCharset);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }

    public boolean verifySign(String algo, Map<String, String> params, byte[] sign){
        Signature signer = initSign(algo);
        if( signer == null )
            return false;

        try {
            signer.initVerify(this.PublicKey);
        } catch (InvalidKeyException e) {
            return false;
        }

        try {
            String data = Hash.BuildParams(params);
            signer.update(data.getBytes(Constants.DefaultCharset));
            return signer.verify(sign);
        } catch (SignatureException e) {
            return false;
        }
    }

    public Buffer sign(String algo, Map<String, String> params){
        Signature signer = initSign(algo);
        if( signer == null )
            return null;

        try {
            signer.initSign(this.PrivateKey);
        } catch (InvalidKeyException e) {
            return null;
        }

        try {
            String data = Hash.BuildParams(params);
            signer.update(data.getBytes(Constants.DefaultCharset));
            return new Buffer(signer.sign());
        } catch (SignatureException e) {
            return null;
        }
    }

    /**
     * @param algo
     * 签名算法，可选如下：
     * NONEwithRSA
     * MD2withRSA
     * MD5withRSA
     * SHA1withRSA (DEFAULT)
     * SHA224withRSA
     * SHA256withRSA
     * SHA384withRSA
     * SHA512withRSA
     * SHA512/224withRSA
     * SHA512/256withRSA
     * SHA3-224withRSA
     * SHA3-256withRSA
     * SHA3-384withRSA
     * SHA3-512withRSA
     * @return Signature
     */
    private static Signature initSign(String algo){
        if( algo == null || algo.equals("") )
            algo = "SHA1withRSA";

        Signature signer = null;
        try {
            signer = Signature.getInstance(algo);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        return signer;
    }

    private static Cipher initCipher(int mode, Key key){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            return null;
        }

        try {
            cipher.init(mode, key);
        } catch (InvalidKeyException e) {
            return null;
        }

        return cipher;
    }
}
