package net.zhuruoling;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

//TODO:用的AES/ECB/PKCS5Padding
public class EncryptedConnector {
    private final BufferedReader in;
    private final PrintWriter out;
    private final byte[] key;
    private final Logger logger = LoggerFactory.getLogger("EncryptedConnector");
    @Contract(pure = true)
    public EncryptedConnector(BufferedReader in, PrintWriter out, @NotNull String key){
        this.in = in;
        this.out = out;
        //补全长度
        if (key.length() <= 16){
            StringBuilder keyBuilder = new StringBuilder(key);
            while (keyBuilder.length() < 16)
                keyBuilder.append("0");
            key = keyBuilder.toString();
        }
        else {
            if (key.length() <= 32) {
                StringBuilder keyBuilder = new StringBuilder(key);
                while (keyBuilder.length() < 32)
                    keyBuilder.append("0");
                key = keyBuilder.toString();
            }
            else {
                throw new RuntimeException();
            }
        }
        this.key = key.getBytes(StandardCharsets.UTF_8);
    }
    public void println(@NotNull String content) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.send(content);
    }

    public void send(@NotNull String content) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var data = encryptECB(content.getBytes(StandardCharsets.UTF_8),this.key);
        logger.info("Sending:" + content);
        out.println(new String(data,StandardCharsets.UTF_8));
        out.flush();
    }

    public String readLine() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String line = in.readLine();
        if (line == null) return null;
        logger.debug("Received:" + line);
        var data = decryptECB(line.getBytes(StandardCharsets.UTF_8),this.key);
        return new String(data,StandardCharsets.UTF_8);
    }

    private static byte[] encryptECB(byte[] data, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        var result = cipher.doFinal(data);
        return Base64.getEncoder().encode(result);
    }

    private static byte[] decryptECB(byte[] data, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
        byte[] base64 = Base64.getDecoder().decode(data);
        return cipher.doFinal(base64);
    }

    public BufferedReader getIn() {
        return in;
    }

    public byte[] getKey() {
        return key;
    }

    public PrintWriter getOut() {
        return out;
    }

}
