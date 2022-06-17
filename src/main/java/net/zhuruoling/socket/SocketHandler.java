package net.zhuruoling.socket;

import net.zhuruoling.EncryptedConnector;
import net.zhuruoling.command.Command;
import org.slf4j.Logger;

import java.net.Socket;
@Deprecated
public abstract class SocketHandler {
    public SocketHandler(Socket socket) {

    }

    public abstract void handle(Command command, Logger logger, EncryptedConnector encryptedConnector);
}
