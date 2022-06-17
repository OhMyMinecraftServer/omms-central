package net.zhuruoling.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.EncryptedConnector;
import net.zhuruoling.command.CommandBuilderKt;
import net.zhuruoling.command.CommandManager;
import net.zhuruoling.message.MessageBuilderKt;
import net.zhuruoling.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class SessionServer extends Thread {
    private final Session session;
    private EncryptedConnector encryptedConnector = null;
    Logger logger = LoggerFactory.getLogger("SessionServer");
    public SessionServer(Session session){
        this.session = session;
        var socket = this.session.socket;
        this.setName(String.format("SessionServer#%s:%d",socket.getInetAddress(), socket.getPort()));
        try {
            this.encryptedConnector = new EncryptedConnector(
                    new BufferedReader(
                            new InputStreamReader(this.session.socket.getInputStream())
                    ),
                    new PrintWriter(
                            new OutputStreamWriter(this.session.socket.getOutputStream())
                    ),
                    new String(
                            this.session.key
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
                    var command = CommandBuilderKt.buildFromJson(line);
                    logger.info("Received " + command);
                    Objects.requireNonNull(CommandManager.INSTANCE.getCommandHandler(Objects.requireNonNull(command).getCmd())).handle(command,new HandlerSession(encryptedConnector,session));
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
