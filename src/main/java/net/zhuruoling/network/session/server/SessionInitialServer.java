package net.zhuruoling.network.session.server;

import net.zhuruoling.main.RuntimeConstants;
import net.zhuruoling.network.session.InitSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

public class SessionInitialServer extends Thread {
    Logger logger = LoggerFactory.getLogger("SessionInitialServer");

    public SessionInitialServer() {
        this.setName("SessionInitialServer#" + this.getId());
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(Objects.requireNonNull(RuntimeConstants.INSTANCE.getConfig()).getPort())) {
            while (true) {
                logger.info("Started SessionInitialServer.");
                var socket = server.accept();
                logger.debug(String.valueOf(socket.getKeepAlive()));
                socket.setKeepAlive(true);
                InitSession session = new InitSession(socket);
                session.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
