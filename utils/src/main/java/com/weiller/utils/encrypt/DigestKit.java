package com.weiller.utils.encrypt;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * DigestKit @version 1.0
 */
public abstract class DigestKit {

    public static byte[] sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(text.getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] md5(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static String encodeBase64(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    public static byte[] decodeBase64(String str) {
        return DatatypeConverter.parseBase64Binary(str);
    }

    public static String md5Hex(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(text.getBytes(Charset.forName("UTF-8")));
            return toHexString(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5Hex(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(input);
            return toHexString(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
