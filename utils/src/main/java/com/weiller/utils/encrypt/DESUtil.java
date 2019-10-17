//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.weiller.utils.encrypt;

import com.weiller.utils.encrypt.ZIPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.codec.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Arrays;

@Slf4j
public class DESUtil {

    public DESUtil() {
    }

    private static byte[] encryptOrDecrypt(boolean encrypt, byte[] desKey, byte[] data) {
        if (data != null && data.length > 0) {
            try {
                SecureRandom sr = new SecureRandom();
                DESKeySpec dks = new DESKeySpec(desKey);
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey key = keyFactory.generateSecret(dks);
                Cipher cipher;
                if (encrypt) {
                    cipher = Cipher.getInstance("DES");
                    cipher.init(1, key, sr);
                } else {
                    cipher = Cipher.getInstance("DES/ECB/NoPadding");
                    cipher.init(2, key, sr);
                }

                return cipher.doFinal(data);
            } catch (Exception var8) {
                log.error("", var8);
                return null;
            }
        } else {
            return null;
        }
    }

    public static char[] encrypt(byte[] data, byte[] desKey) throws Exception {
        return new char[0];
    }

    public static byte[] decrypt(char[] data, byte[] desKey) throws Exception {
        return encryptOrDecrypt(false, desKey, Base64.decode(Arrays.toString(data).getBytes(Charset.defaultCharset())));
    }

    public static String encrypt(String data, String key) throws Exception {
        return new String(Base64.encode(encryptOrDecrypt(true, key.getBytes(Charset.defaultCharset()), data.getBytes(Charset.defaultCharset()))), Charset.defaultCharset());
    }

    public static String decrypt(String data, String key) throws Exception {
        return (new String(encryptOrDecrypt(false, key.getBytes(Charset.defaultCharset()), Base64.decode(data.getBytes(Charset.defaultCharset()))), Charset.defaultCharset())).trim();
    }

    public static String encrypt(String data, byte[] key, boolean zip) throws Exception {
        if (data == null) {
            return null;
        } else {
            byte[] bytes;
            if (zip) {
                bytes = ZIPUtil.compress(data.getBytes(Charset.defaultCharset()));
            } else {
                bytes = data.getBytes(Charset.defaultCharset());
            }

            return new String(Base64.encode(encryptOrDecrypt(true, key, bytes)), Charset.defaultCharset());
        }
    }

    public static String decrypt(String data, byte[] key, boolean zip) throws Exception {
        if (data == null) {
            return null;
        } else {
            byte[] bytes = encryptOrDecrypt(false, key, Base64.decode(data.getBytes(Charset.defaultCharset())));
            if (zip) {
                bytes = ZIPUtil.decompress(bytes);
            }

            return (new String(bytes, Charset.defaultCharset())).trim();
        }
    }

    public static String decrypt(String data, String key, boolean zip) throws Exception {
        return decrypt(data, key.getBytes(Charset.defaultCharset()), zip);
    }

    public static String encrypt(String data, String key, boolean zip) throws Exception {
        return encrypt(data, key.getBytes(Charset.defaultCharset()), zip);
    }
}
