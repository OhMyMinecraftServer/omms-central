package net.zhuruoling.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.EncryptedConnector;
import net.zhuruoling.command.Command;
import net.zhuruoling.command.CommandBuilderKt;
import net.zhuruoling.configuration.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class SocketHandler extends Thread {

    private final EncryptedConnector encryptedConnector; //用这个类接下所有的socket
    private final Logger logger = LoggerFactory.getLogger("client-handler");
    private final Gson json = new GsonBuilder().serializeNulls().create();

    public SocketHandler(Socket socket) throws IOException {
        super(String.format("SocketHandler#%s:%d",socket.getInetAddress(), socket.getPort()));
        var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        var out = new PrintWriter(socket.getOutputStream(), true);
        this.encryptedConnector = new EncryptedConnector(in, out, Objects.requireNonNull(ConfigReader.read()).getCryptoKey());
        logger.info("client:" + socket.getInetAddress() + ":" + socket.getPort() + " connected.");
        this.start();
    }

    @Override
    public void run() {
        try {
            String line = encryptedConnector.readLine();
            while (line != null){
                try{
                    handle(CommandBuilderKt.buildFromJson(line), logger, encryptedConnector);
                }
                catch (Exception e){
                    e.printStackTrace();
                    break;
                }
                line = encryptedConnector.readLine();
            }
        }
        catch (Exception e){
            e.printStackTrace();

        }

    }

    public abstract void handle(Command command,Logger logger,EncryptedConnector encryptedConnector);

}
