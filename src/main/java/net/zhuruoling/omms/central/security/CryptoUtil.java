package net.zhuruoling.omms.central.security;

import cn.hutool.core.compress.Gzip;
import io.ktor.utils.io.core.Output;
import net.zhuruoling.omms.central.util.Util;
import org.apache.tools.ant.filters.StringInputStream;
import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CryptoUtil {

    public static String toPaddedAesKey(String src) {
        String key = src;
        if (key.length() <= 16) {
            StringBuilder keyBuilder = new StringBuilder(key);
            while (keyBuilder.length() < 16)
                keyBuilder.append("0");
            key = keyBuilder.toString();
        } else {
            if (key.length() <= 32) {
                StringBuilder keyBuilder = new StringBuilder(key);
                while (keyBuilder.length() < 32)
                    keyBuilder.append("0");
                key = keyBuilder.toString();
            } else {
                throw new RuntimeException();
            }
        }
        return key;
    }

    public static String aesEncryptToB64String(String data, String key) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return new String(
                encryptECB(
                        data.getBytes(StandardCharsets.UTF_8),
                        toPaddedAesKey(key).getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    public static String aesDecryptFromB64String(String data, String key) throws Exception {
        return new String(decryptECB(data.getBytes(StandardCharsets.UTF_8), toPaddedAesKey(key).getBytes(StandardCharsets.UTF_8)));
    }

    public static byte[] encryptECB(byte @NotNull [] data, byte @NotNull [] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        var result = cipher.doFinal(data);
        return Base64.getEncoder().encode(result);
    }

    public static byte[] decryptECB(byte[] data, byte @NotNull [] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
        byte[] base64 = Base64.getDecoder().decode(data);
        return cipher.doFinal(base64);
    }

    public static String gzipCompress(String source) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(source.getBytes(StandardCharsets.UTF_8));
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Util.base64Encode(out.toString());
    }

    public static String gzipDecompress(String source){
        ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8)));
        try (GZIPInputStream stream = new GZIPInputStream(in)){
            return new String(stream.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}