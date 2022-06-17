package net.zhuruoling.plugins;

import net.zhuruoling.message.MessageBuilderKt;
import net.zhuruoling.session.HandlerSession;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class RequestServerInterface {
    private HandlerSession session;

    public RequestServerInterface(HandlerSession session) {
        this.session = session;
    }

    public void sendBack(String code, String[] load) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var message = MessageBuilderKt.build(code,load);
        session.getEncryptedConnector().println(message);
    }
}
