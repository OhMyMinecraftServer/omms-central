package net.zhuruoling.omms.central.network.session;

import net.zhuruoling.omms.central.network.EncryptedSocket;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class RateLimitEncryptedSocket {
    private final EncryptedSocket encryptedSocket;
    private final int rateLimit;
    private final boolean enable;
    private int count = 0;
    private long time;

    public RateLimitEncryptedSocket(EncryptedSocket encryptedSocket, int rateLimit) {
        this.encryptedSocket = encryptedSocket;
        this.rateLimit = rateLimit;
        enable = rateLimit >= 1;
        time = System.currentTimeMillis();
    }

    public static RateLimitEncryptedSocket of(EncryptedSocket encryptedSocket, int limit) {
        return new RateLimitEncryptedSocket(encryptedSocket, limit);
    }

    public Request receiveRequest() throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var line = this.encryptedSocket.readLine();
        if (enable) {
            if (time - System.currentTimeMillis() >= 1000L){
                time = System.currentTimeMillis();
                count = 1;
            }else {
                count++;
                if (count > rateLimit) {
                    throw new RateExceedException("Current speed limit exceeded");
                }
            }
        }
        return Util.fromJson(line, Request.class);
    }

    public void sendResponse(Response response) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var line = Util.toJson(response);
        encryptedSocket.println(line);
    }
}
