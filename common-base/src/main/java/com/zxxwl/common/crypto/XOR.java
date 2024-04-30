package com.zxxwl.common.crypto;



import com.zxxwl.common.utils.Constants;

import java.util.Locale;

public class XOR {

    public static byte[] hex2bin(String data){
        int length = data.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2){
            int h = Integer.parseInt(data.substring(i, i + 1), 16);
            int l = Integer.parseInt(data.substring(i + 1, i + 2), 16);
            bytes[i / 2] = (byte)(h * 16 + l);
        }

        return bytes;
    }

    public static String bin2hex(byte[] bytes){
        StringBuilder buffer = new StringBuilder();
        for (byte code : bytes)
            buffer.append(String.format(Locale.ENGLISH, "%02x", code));

        return buffer.toString();
    }

    public static String crypt(String data, String key){
        byte[] buffer = data.getBytes(Constants.DefaultCharset);
        byte[] codes  = key.getBytes(Constants.DefaultCharset);

        int keySize = codes.length;
        int size = buffer.length;
        byte[] result = new byte[size];
        for(int i = 0; i < size; i++)
            result[i] = (byte)(buffer[i] ^ codes[i % keySize]);

        return new String(result, Constants.DefaultCharset);
    }
}
