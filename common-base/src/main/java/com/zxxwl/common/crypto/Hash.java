package com.zxxwl.common.crypto;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

@Slf4j
public class Hash {
    public static String Algorithm(String algo){
        algo = algo.toUpperCase();

        switch (algo){
            case "MD2":
            case "MD5":
            case "SHA-1":
            case "SHA-224":
            case "SHA-256":
            case "SHA-384":
            case "SHA-512":
            case "SHA-512/224":
            case "SHA-512/256":
            case "SHA3-224":
            case "SHA3-256":
            case "SHA3-384":
            case "SHA3-512":
                return algo;
            case "SHA1":
                algo = "SHA-1";
                return algo;
            case "SHA224":
                algo = "SHA-224";
                return algo;
            case "SHA256":
                algo = "SHA-256";
                return algo;
            case "SHA384":
                algo = "SHA-384";
                return algo;
                default:
                algo = "MD5";break;
        }

        return algo;
    }

    private static String digest(String data, String algo){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(Algorithm(algo));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        int length = data.length();
        if( length > 1024 ){
            for(int i = 0; i < length; i += 1024){
                int end = i + 1024;
                end = end > length ? length : end;
                byte[] bytes = data.substring(i, end).getBytes();
                md.update(bytes);
            }
        }
        else
            md.update(data.getBytes());

        byte[] codes = md.digest();
        StringBuilder builder = new StringBuilder();
        for (byte code : codes)
            builder.append(String.format(Locale.CHINESE, "%02x", code));

        return builder.toString();
    }

    /**
     * @param data 待hash字符串
     * @param algo hash算法
     * 可选算法，如下：
     * MD2
     * MD5
     * SHA-1
     * SHA-224
     * SHA-256
     * SHA-384
     * SHA-512
     * SHA-512/224
     * SHA-512/256
     * SHA3-224
     * SHA3-256
     * SHA3-384
     * SHA3-512
     * @return String
     */
    public static String build(String data, String algo){
        algo = algo.toUpperCase();
        return digest(data, algo);
    }

    private static MessageDigest getDigest(String algo){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(Algorithm(algo));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        return md;
    }

    private static String buildHash(MessageDigest md, InputStream input){
        byte[] bytes = new byte[1024];

        while (true) {
            try {
                int size = input.read(bytes, 0, 1024);
                if( size == -1)
                    break;

                md.update(bytes);
            } catch (IOException e) {
                return null;
            }
        }

        try {
            input.close();
        } catch (IOException e) {
            log.error("{}",e.getMessage());
        }

        byte[] result = md.digest();
        StringBuilder builder = new StringBuilder();
        for (byte code : result)
            builder.append(String.format(Locale.CHINESE, "%02x", code));

        return builder.toString();
    }

    public static String stream(InputStream input){
        return stream(input, "MD5");
    }

    public static String stream(InputStream input, String algo){
        if( input == null )
            return null;

        MessageDigest md = getDigest(algo);
        if( md == null )
            return null;

        return buildHash(md, input);
    }

    public static String file(String path){
        File file = new File(path);
        if( !(file.exists() && file.isFile()) )
            return null;

        return file(path, "MD5");
    }

    public static String file(String path, String algo){
        File file = new File(path);
        if( !(file.exists() && file.isFile()) )
            return null;

        FileInputStream input;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }

        MessageDigest md = getDigest(algo);
        if( md == null )
            return  null;

        return buildHash(md, input);
    }

    public static String md5(byte[] bytes){
        MessageDigest md = getDigest("MD5");
        if( md == null )
            return null;

        md.update(bytes);

        byte[] result = md.digest();
        StringBuilder builder = new StringBuilder();
        for (byte code : result)
            builder.append(String.format(Locale.CHINESE, "%02x", code));

        return builder.toString();
    }

    public static String md5(String data){
        return md5(data, false);
    }

    public static String md5(String data, boolean simple){
        if( data == null || data.isEmpty())
            return null;

        String rs = digest(data, "MD5");
        if( rs == null )
            rs = "";

        return simple ? rs.substring(8, 24) : rs;
    }

    public static String sha1(String data){
        if( data == null || data.isEmpty())
            return null;

        return digest(data, "SHA-1");
    }

    public static String sha256(String data){
        if( data == null || data.isEmpty())
            return null;

        return digest(data, "SHA-256");
    }

    public static String sha512(String data){
        if( data == null || data.isEmpty())
            return null;

        return digest(data, "SHA-512");
    }

    public static String sign(Map<String, String> params, String algo){
        return Hash.build(BuildParams(params), algo);
    }

    public static String BuildParams(Map<String, String> params){
        TreeSet<String> keys = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String k1, String k2) {
                return k1.compareTo(k2);
            }
        });
        keys.addAll(params.keySet());

        StringBuilder builder = new StringBuilder();
        for (String key : keys)
            builder.append("&").append(key).append("=").append(params.get(key));

        return builder.substring(1);
    }

    public static String bcrypt(String data){

        return "";
    }
}
