package net.zhuruoling.omms.central.network.session.server;

import net.zhuruoling.omms.central.GlobalVariable;
import net.zhuruoling.omms.central.network.session.LoginSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

public class SessionLoginServer extends Thread {
    final Logger logger = LoggerFactory.getLogger("SessionLoginServer");

    public SessionLoginServer() {
        this.setName("SessionLoginServer#" + this.getId());
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(Objects.requireNonNull(GlobalVariable.INSTANCE.getConfig()).getPort())) {
            logger.info("Started SessionLoginServer.");
            while (true) {
                var socket = server.accept();
                logger.debug(String.valueOf(socket.getKeepAlive()));
                socket.setKeepAlive(true);
                LoginSession session = new LoginSession(socket);
                session.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
