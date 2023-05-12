package net.zhuruoling.omms.central.network;

import net.zhuruoling.omms.central.security.CryptoUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

//TODO:用的AES/ECB/PKCS5Padding
public class EncryptedSocket {
    private final BufferedReader in;
    private final PrintWriter out;
    private final byte @NotNull [] key;
    private final Logger logger = LoggerFactory.getLogger("EncryptedSocket");

    @Contract(pure = true)
    public EncryptedSocket(BufferedReader in, PrintWriter out, @NotNull String key) {
        this.in = in;
        this.out = out;
        this.key = CryptoUtil.toPaddedAesKey(key).getBytes(StandardCharsets.UTF_8);
    }

    public void println(@NotNull String content) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.send(content);
    }

    public void send(@NotNull String content) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var data = CryptoUtil.encryptECB(content.getBytes(StandardCharsets.UTF_8), this.key);
        logger.debug("Sending:" + content);
        out.println(new String(data, StandardCharsets.UTF_8));
        out.flush();
    }

    public @Nullable String readLine() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String line = in.readLine();
        if (line == null) return null;
        logger.debug("Received:" + line);
        var data = CryptoUtil.decryptECB(line.getBytes(StandardCharsets.UTF_8), this.key);
        return new String(data, StandardCharsets.UTF_8);
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