package net.zhuruoling.server;

import net.zhuruoling.configuration.ConfigReader;
import net.zhuruoling.socket.ClientSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {

    final static Logger logger = LoggerFactory.getLogger("SocketServer");
    public SocketServer(){
        this.setName("SocketServer#" + this.getId());
    }

    @Override
    public void run() {
        final Logger logger = LoggerFactory.getLogger("Main");
        ServerSocket server = null;
        try {
            server = new ServerSocket(ConfigReader.read().getPort());
            logger.info("Started Uranium qLink socket service at "+ server.getLocalPort());
            while (true){
                try {
                    Socket socket = server.accept();
                    new ClientSocketHandler(socket);
                }
                catch (Exception e){
                    logger.warn("An error occurred while listening client connections:");
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
