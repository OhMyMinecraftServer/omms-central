package net.zhuruoling.session;

import net.zhuruoling.configuration.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionInitialServer extends Thread {
    ExecutorService service = Executors.newFixedThreadPool(5);
    public SessionInitialServer(){
        this.setName("SessionInitialServer#" + this.getId());
    }

    Logger logger = LoggerFactory.getLogger("SessionInitialServer");
    @Override

    public void run() {
        while (true){
            try {
                logger.info("Started SessionInitialServer.");
                ServerSocket server = new ServerSocket(Objects.requireNonNull(ConfigReader.read()).getPort());
                var socket = server.accept();
                InitSession session = new InitSession(socket);
                session.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
