package net.zhuruoling.network.session.server;

import net.zhuruoling.network.EncryptedConnector;
import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.Session;
import net.zhuruoling.network.session.request.RequestBuilderKt;
import net.zhuruoling.network.session.request.RequestManager;
import net.zhuruoling.permission.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SessionServer extends Thread {
    private final Session session;
    private EncryptedConnector encryptedConnector = null;
    final Logger logger = LoggerFactory.getLogger("SessionServer");
    List<Permission> permissions;
    public SessionServer(Session session, List<Permission> permissions){
        this.session = session;
        this.permissions = permissions;
        var socket = this.session.getSocket();
        this.setName(String.format("SessionServer#%s:%d",socket.getInetAddress(), socket.getPort()));
        try {
            this.encryptedConnector = new EncryptedConnector(
                    new BufferedReader(
                            new InputStreamReader(this.session.getSocket().getInputStream())
                    ),
                    new PrintWriter(
                            new OutputStreamWriter(this.session.getSocket().getOutputStream())
                    ),
                    new String(
                            this.session.getKey()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (socket.isClosed()){
            throw new RuntimeException();
        }
    }

    @Override
    public void run() {
        logger.info("%s started.".formatted(this.getName()));
        String line;
        try {
            line = encryptedConnector.readLine();
            while (true){
                try {
                    if (session.getSocket().isClosed())
                        break;
                    var command = RequestBuilderKt.buildFromJson(line);
                    logger.info("Received " + command);
                    Objects.requireNonNull(RequestManager.INSTANCE.getRequestHandler(Objects.requireNonNull(command).getRequest())).handle(command,new HandlerSession(encryptedConnector,session,this.permissions));
                    if (session.getSocket().isClosed())
                        break;
                    line = encryptedConnector.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

}
