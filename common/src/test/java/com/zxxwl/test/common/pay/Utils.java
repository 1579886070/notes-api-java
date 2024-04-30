package com.zxxwl.test.common.pay;

import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Utils {

    //私钥，开发者妥善保存
    public static String DevPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC3ddKXwnGTdHixkRbj4CND9P6EP1iQH5qWUL/yURfEPsnAiRdtJ0VduzQxCTVoKYxMunQdxl4Y28VCHgp0mlVqRpIWsmvU5To4FKz8f9oVzRRgJQfhxv6ZYaRBwvSw0xfv1zCLRR2nWU4WrxqLXtakH7MMBhkX8VqqltksOkknXzWNZBgwpL39kpXkStrs6mfdzyewSK4u8doTuQgc5wz0YFGZuMvYZbqQO/0WMKUv2kHvRK19cYQIHYQSHFZMujPwSC/26x4FZ+XeNXRq/CDovK9psED+EjmiqOf8PnwCMYdHsOuyybTlvNqoeFfLJtf9AkBB7lZgx97gmMiW5TnLAgMBAAECggEBALGj8pBkBNmEs8Er/gAUbdFFjpS7SaP0FhlqgxG9F8g8C9rUY8ybc/04YWcBxabgZCt/dyPFiiOD2dMVh4Y0UnSfdKFpjiQM+XyH+KBdo+vsMciO0rz5lFksRIIwpgH5xHnF4glYmUgLnyMz1f4RjMBKuXHxDaczWllHy22Z2m+1ikOROuz3Dy/50uWvp9o1JMguXYpTIT6LRJw5DtI1sYeZk5Ae0vJRahtPoQR/1TOwKOlBGCGFBCqE4s60oQnIFewJJXatUM1y14AJ2nVUBofcBLfVz28kXykqCtkOhtZd1OgCzXdYm/dSKVbdjOO7VP+U/TklKRSS/d1nQwS0lfECgYEA6KwdQnGf5sK62kCRFh/Ys3bl/WnAAEPOIX4iZr7ry66XYtSxHWG5tk++EjNg/oqG83DqDeeXAqn+79DbSS+1Uke+tdLsFepMvLc1EBfsp39CU9eKoxtS22LH1EKoc0vus20LvrCAIW3bWQNFt4druyOaIxrzmPpdxHYmpGAH3y8CgYEAydqbMgXZz82FVe6jStEI5e2ZTlJwp1P4IZqPUPP6Sa7B6JctFwx3kh1KcsWyeiKAAsQ/+eZWq4z+AH5AwPRqKhZVqABvmmPBOCG2GMKBnhMxauzt/RJwsVKqXZw6WqJYgIRW6DWurtxMfWOfc+AicusiA1MzTlUfrdzoHWefiCUCgYAvrDRCjO1qZrPKTDhuBBBulQoCpkxEwZ/WlPWPf8bocNlr0pCHqKJYjHYxXH2fKdsEvjn2p2D1lsc5c926XSAVv8V/k7vzsZIPiKpCpeRDXXvFmgA58ztC4DZ5cz74gLJj6Q/i/l8CenPbBSSfuguEyIKodrk6uvHDc0HvpA142QKBgDFhsrAFMGkfDWydagNmqjed7fB0SK0W5NBLwJ38YhHel/+v5E8MNMBhPBJh/SzV/d0vZ7taN3/WiVlTicHqyYIsXp4wJR1vJBj4XlUmWJF0fpV7CynrdfhbkeQ+0SPVLbObLKu4XljW+jt+wMaXYbYdgf+wPg/+0TY2oe53qFalAoGAcALDgQXnP7QTc//TIXJcQO8RBIDzWyEo/kr9UMcKcFOhRHMVep4GPfUo3rnZc286eJrDPD9OZfg9rb6XGjI7M0k/kzYwbfn2mCHE3XIHYhN+enpndqUk4SO4LMCWNxUYFNP/2Pcc7DMZv55bM0nq0qe5nhxIL5ypBoa6Xv0/8VQ=";
//    //对应的公钥，提供给钱宝
//    public static String DevPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt3XSl8Jxk3R4sZEW4+AjQ/T+hD9YkB+allC/8lEXxD7JwIkXbSdFXbs0MQk1aCmMTLp0HcZeGNvFQh4KdJpVakaSFrJr1OU6OBSs/H/aFc0UYCUH4cb+mWGkQcL0sNMX79cwi0Udp1lOFq8ai17WpB+zDAYZF/FaqpbZLDpJJ181jWQYMKS9/ZKV5Era7Opn3c8nsEiuLvHaE7kIHOcM9GBRmbjL2GW6kDv9FjClL9pB70StfXGECB2EEhxWTLoz8Egv9useBWfl3jV0avwg6LyvabBA/hI5oqjn/D58AjGHR7Drssm05bzaqHhXyybX/QJAQe5WYMfe4JjIluU5ywIDAQAB";




    //平台公钥，由钱宝提供
    public static String QBPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsfOVR3OUJDm4C355mAVo+7zYLY3sm+yX0DGe7SEWhKRVXz1boceOiJjkJ9dhdJssHnIsl3n6rKo0Oj8MgJ3wAZ2tRCttFBtcK8pXL+YlDlrDOpDB5Pph9BpVCzyGLWyALwyRlMHXXSuqM01+7RMbY7ggtjmkVlv3J9IZ/yvuKkN6fvNUkaeyjX1xSNNl30mFMTCQDS5sAEppclLGfUUI7iJsZendHamlLI8ejt5UmfZgW5MyJuJagiRq4XNgO6YY57gmr6O/OxH98u1pYdt6dRx+mCENHcJjRHpGEFvXd5vSSDhRhKCF2udxKlhB3oqhav29xy5jgnhzYZutNASnNQIDAQAB";
    public static String server = "http://119.23.124.194:60002";
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static String httpPost(String url, String data, Map<String, String> headers, Map<String, String> respHeaders) {
        return Utils.httpPost(url, data, headers, respHeaders, StandardCharsets.UTF_8, true);
    }

    public static String httpPost(String url, String data, Map<String, String> headers, Map<String, String> respHeaders, Charset charset, boolean logInfo) {
        if (logInfo) {
            logMsg(String.format("请求：%s\r\n%s\n", url, data));
        }
        try {
            var uri = new URL(url);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) uri.openConnection();
            connection.setRequestMethod("POST");
            if (headers != null && headers.size() > 0) {
                try {
                    Iterator<String> iter = headers.keySet().iterator();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        String val = headers.get(key);
                        if (key != null && key.trim().length() > 0 && val != null) {
                            connection.setRequestProperty(key, val);
                            if (logInfo) {
                                logMsg(String.format("%s : %s", key, val));
                            }
                        }
                    }
                    if (headers.size() > 1) {
                        logMsg(headers.toString());
                    }
                } catch (Exception ignore) {
                }
            }
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setReadTimeout(60 * 1000);
            connection.setConnectTimeout(10 * 1000);

            var out = connection.getOutputStream();
            out.write(data.getBytes(charset));
            out.flush();
            out.close();

            Map<String, List<String>> map = connection.getHeaderFields();

            for (String key : map.keySet()) {
                try {
                    respHeaders.put(key, map.get(key).get(0));
                } catch (Exception ignore) {
                }
            }
            var res = readStream(connection.getInputStream());
            if (logInfo) {
                logMsg(String.format("响应码： %s， 响应：%s， %s\n\n", connection.getResponseCode(), res, connection.getResponseMessage()));
            }
            return res;
        } catch (Exception e) {
            logMsg(e);
            return null;
        }
    }

    public static void logMsg(Exception e) {
        e.printStackTrace();
    }

    public static void logMsg(String msg) {
        System.out.println(msg);
    }

    public static PrivateKey getPrivateKey(String priKey) {
        PrivateKey privateKey = null;
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(priKey.getBytes()));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            privateKey = keyf.generatePrivate(priPKCS8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static PublicKey getPublicKey(String pubKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec PubKeySpec = new X509EncodedKeySpec(
                    Base64.decode(pubKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            publicKey = keyFactory.generatePublic(PubKeySpec);
        } catch (Exception e1) {
        }
        return publicKey;
    }

    //SHA256withRSA签名
    public static String sign256(String data, String privateKey) {
        byte[] sigByte = Utils.sign256(data.getBytes(StandardCharsets.UTF_8), getPrivateKey(privateKey));
        String signVal = Base64.toBase64String(sigByte);
        return signVal;
    }

    //SHA256withRSA签名
    public static byte[] sign256(byte[] data, PrivateKey privateKey) {
        byte[] signed = null;
        Signature signature = null;
        try {
            signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);
            signed = signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signed;
    }

    //SHA256withRSA验签
    public static boolean verify256(String data, String sign, String pubKey) {
        if (data == null || sign == null) {
            return false;
        }
        try {
            byte[] signBytes = Base64.decode(sign);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            Signature signetcheck = Signature.getInstance(SIGNATURE_ALGORITHM);
            signetcheck.initVerify(getPublicKey(pubKey));
            signetcheck.update(dataBytes);
            return signetcheck.verify(signBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readStream(InputStream stream) {
        String str = null;
        try {
            byte[] buffer = new byte[64];
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int read;
            while ((read = stream.read(buffer)) > 0) {
                bo.write(buffer, 0, read);
            }
            str = bo.toString(StandardCharsets.UTF_8);
        } catch (Exception ignore) {
        }
        return str;
    }

}
