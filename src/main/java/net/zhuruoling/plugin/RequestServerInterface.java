package net.zhuruoling.plugin;

import net.zhuruoling.message.MessageBuilderKt;
import net.zhuruoling.network.session.HandlerSession;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class RequestServerInterface extends ServerInterface {
    public RequestServerInterface(HandlerSession session, String name) {
        super(session, name);
    }

    public void sendBack(String code, String[] load) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var message = MessageBuilderKt.build(code,load);
        this.getSession().getEncryptedConnector().println(message);
    }
}

