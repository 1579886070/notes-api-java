package com.zxxwl.common.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.StringJoiner;

/**
 * @author qingyu 2023.05.12
 */
public class CryptoUtil {
    /**
     * @param input input
     * @return encode
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public static String md5v2(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringJoiner hexString = new StringJoiner("");
        for (byte t : hash) {
            hexString.add(String.format("%02x", t));
        }
        return hexString.toString();
    }

    public static String md5v202(String input) {
        try {
            // 获取MD5消息摘要实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将字符串转换为字节数组，并计算其哈希值
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not fou");
        }
    }

    /**
     * 第一次执行较慢
     *
     * @param input input
     * @return encode
     */
//    public static String md5v3(String input) {
//        return DigestUtils.md5Hex(input);
//    }
    public static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringJoiner hexString = new StringJoiner("");
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.add("0");
            hexString.add(hex);
        }
        return hexString.toString();
    }
}
