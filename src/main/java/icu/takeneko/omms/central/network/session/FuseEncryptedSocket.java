package icu.takeneko.omms.central.network.session;

import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.EncryptedSocket;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FuseEncryptedSocket {
    private final EncryptedSocket encryptedSocket;
    private final int rateLimit;
    private final boolean enable;
    private int count = 0;
    private long time;

    public FuseEncryptedSocket(EncryptedSocket encryptedSocket, int rateLimit) {
        this.encryptedSocket = encryptedSocket;
        this.rateLimit = rateLimit;
        enable = rateLimit >= 1;
        time = System.currentTimeMillis();
    }

    public static @NotNull FuseEncryptedSocket of(EncryptedSocket encryptedSocket, int limit) {
        return new FuseEncryptedSocket(encryptedSocket, limit);
    }

    public Request receiveRequest() throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var line = this.encryptedSocket.readLine();
        if (enable) {
            if (System.currentTimeMillis() - time >= 1000L){
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

    public void sendResponse(@NotNull Response response) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var line = Util.toJson(response);
        encryptedSocket.println(line);
    }
}
