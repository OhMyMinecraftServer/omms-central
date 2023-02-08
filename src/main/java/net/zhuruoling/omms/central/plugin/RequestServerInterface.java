package net.zhuruoling.omms.central.plugin;

import net.zhuruoling.omms.central.network.session.message.MessageBuilderKt;
import net.zhuruoling.omms.central.network.session.SessionContext;
import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class RequestServerInterface extends ServerInterface {
    public RequestServerInterface(SessionContext session, String name) {
        super(session, name);
    }
}

