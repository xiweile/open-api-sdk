//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.weiller.utils.encrypt;

import org.springframework.security.crypto.codec.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZIPUtil {
    private static final int BUFFER_SIZE = 2048;

    public ZIPUtil() {
    }

    public static String decompress(String encdata, boolean codebyB64) throws IOException {
        if (encdata == null) {
            return null;
        } else {
            return codebyB64 ? new String(decompress(Base64.decode(encdata.getBytes(Charset.defaultCharset()))), Charset.defaultCharset()) : new String(decompress(encdata.getBytes(Charset.defaultCharset())), Charset.defaultCharset());
        }
    }

    public static String compress(String data, boolean codebyB64) throws IOException {
        if (data == null) {
            return null;
        } else {
            return codebyB64 ? new String(Base64.encode(compress(data.getBytes(Charset.defaultCharset()))), Charset.defaultCharset()) : new String(compress(data.getBytes(Charset.defaultCharset())), Charset.defaultCharset());
        }
    }

    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        DeflaterOutputStream deflaterOutputStream = null;

        byte[] var4;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
            deflaterOutputStream.write(data);
            deflaterOutputStream.close();
            var4 = byteArrayOutputStream.toByteArray();
        } finally {
            if (deflaterOutputStream != null) {
                deflaterOutputStream.close();
            }

            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }

        }

        return var4;
    }

    public static byte[] decompress(byte[] encdata) throws IOException {
        if (encdata == null) {
            return null;
        } else {
            InputStream inputStream = null;
            InflaterInputStream inflaterInputStream = null;
            ByteArrayOutputStream byteArrayOutputStream = null;

            try {
                inputStream = new ByteArrayInputStream(encdata);
                inflaterInputStream = new InflaterInputStream(inputStream);
                byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] data = new byte[2048];

                int count;
                while((count = inflaterInputStream.read(data, 0, 2048)) != -1) {
                    byteArrayOutputStream.write(data, 0, count);
                }

                byte[] var7 = byteArrayOutputStream.toByteArray();
                return var7;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (inflaterInputStream != null) {
                    inflaterInputStream.close();
                }

                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }

            }
        }
    }
}
