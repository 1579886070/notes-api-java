package com.zxxwl.common.crypto;


import com.zxxwl.common.utils.Constants;
import jakarta.validation.constraints.NotNull;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {

    private static final String ALGO = "AES";

    enum Mode {
        CBC,
        ECB
    }

    enum Padding {
        NoPadding,
        PKCS5Padding,
        PKCS7Padding
    }

    public static class Buffer {
        public byte[] result;

        public Buffer(byte[] result) {
            this.result = result;
        }

        public String toBase64() {
            return Base64.getEncoder().encodeToString(this.result);
        }

        public String toHex() {
            return XOR.bin2hex(result);
        }

        public static byte[] FromBase64(String data) {
            return Base64.getDecoder().decode(data);
        }

        public static byte[] FromHex(String data) {
            return XOR.hex2bin(data);
        }
    }

    public static class Transformation {
        Mode mode;
        Padding padding;
        String iv;

        public Transformation(Mode mode, Padding padding) {
            this.mode = mode;
            this.padding = padding;
        }

        public Transformation(Mode mode, Padding padding, String iv) {
            this.mode = mode;
            this.padding = padding;
            this.iv = iv;
        }

        public Transformation setIv(String iv) {
            this.iv = iv;
            return this;
        }

        public String get() {
            StringBuilder builder = new StringBuilder();

            builder.append(ALGO);

            switch (mode) {
                case ECB:
                    builder.append("/ECB");
                    break;
                case CBC:
                default:
                    builder.append("/CBC");
                    break;
            }

            switch (padding) {
                case NoPadding:
                    builder.append("/NoPadding");
                    break;
                case PKCS7Padding:
                    builder.append("/PKCS7Padding");
                    break;
                case PKCS5Padding:
                default:
                    builder.append("/PKCS5Padding");
                    break;
            }

            return builder.toString();
        }
    }

    public static Buffer encrypt(Transformation trans, String data, @NotNull String password) {
        Cipher cipher = initCipher(trans, Cipher.ENCRYPT_MODE, password);
        if (cipher == null)
            return null;

        byte[] bytes = data.getBytes(Constants.DefaultCharset);

        try {
            return new Buffer(cipher.doFinal(bytes));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }

    public static String decrypt(Transformation trans, byte[] bytes, @NotNull String password) {
        Cipher cipher = initCipher(trans, Cipher.DECRYPT_MODE, password);
        if (cipher == null)
            return null;

        try {
            byte[] result = cipher.doFinal(bytes);
            return new String(result, Constants.DefaultCharset);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }

    private static Cipher initCipher(Transformation trans, int mode, String password) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(trans.get());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            return null;
        }

        IvParameterSpec iv = null;
        if (trans.mode == Mode.CBC)
            iv = new IvParameterSpec(trans.iv.getBytes());

        try {
            if (iv != null)
                cipher.init(mode, key(password), iv);
            else
                cipher.init(mode, key(password));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            return null;
        }

        return cipher;
    }

    private static SecretKey key(String seed) {
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        random.setSeed(seed.getBytes());

        KeyGenerator generator = null;
        try {
            generator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        generator.init(128, random);

        byte[] buffer = generator.generateKey().getEncoded();
        return new SecretKeySpec(buffer, "AES");
    }

    private static String iv() {
        return Random.chars(16, false);
    }
}
