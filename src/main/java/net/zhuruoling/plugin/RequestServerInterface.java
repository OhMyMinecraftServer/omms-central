package net.zhuruoling.plugin;

import net.zhuruoling.message.MessageBuilderKt;
import net.zhuruoling.session.HandlerSession;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class RequestServerInterface {
    private final HandlerSession session;
    private PluginLogger logger;
    public RequestServerInterface(HandlerSession session, String name) {
        this.session = session;
        logger = new PluginLogger(name);
    }

    public void sendBack(String code, String[] load) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var message = MessageBuilderKt.build(code,load);
        session.getEncryptedConnector().println(message);
    }

    public PluginLogger getLogger() {
        return logger;
    }
}
