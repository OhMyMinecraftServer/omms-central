package net.zhuruoling.omms.central.network.old.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.omms.central.GlobalVariable;
import net.zhuruoling.omms.central.network.EncryptedSocket;
import net.zhuruoling.omms.central.network.old.session.server.SessionServer;
import net.zhuruoling.omms.central.network.session.request.LoginRequest;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.PermissionManager;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;

public class LoginSession extends Thread {
    private final @NotNull EncryptedSocket encryptedConnector;
    private final Logger logger = LoggerFactory.getLogger("InitSession");
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final @NotNull Socket socket;
    public LoginSession(@NotNull Socket socket) throws IOException {
        super(String.format("LoginSession@%s:%d",socket.getInetAddress(), socket.getPort()));
        var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        var out = new PrintWriter(socket.getOutputStream(), true);
        var date = LocalDateTime.now();
        var key = date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"));
        key = Util.base64Encode(Util.base64Encode(key));
        logger.debug("key:%s".formatted(key));
        this.encryptedConnector = new EncryptedSocket(in, out, key);
        logger.info("Client: " + socket.getInetAddress() + ":" + socket.getPort() + " connected.");
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String line = encryptedConnector.readLine();
            while (true){
                var request = gson.fromJson(line, LoginRequest.class);
                logger.debug("Got request:" + request);
                if (Objects.equals(Objects.requireNonNull(request).getRequest(), "PING")) {
                    if (request.getVersion() != Util.PROTOCOL_VERSION){
                        encryptedConnector.send(
                                Response.serialize(new Response().withResponseCode(Result.VERSION_NOT_MATCH))
                        );
                        break;
                    }
                    String stringToken = request.getContent("token");
                    var authKey = new String(Base64.getDecoder().decode(Base64.getDecoder().decode(stringToken)));
                    var date = Long.parseLong(Util.getTimeCode());
                    var key = Long.parseLong(authKey);
                    long permCode = key ^ date;
                    logger.info("Got permission code %d".formatted(permCode));
                    boolean isCodeExist = !(PermissionManager.INSTANCE.getPermission(
                            (int) permCode
                    ) == null);
                    var permissions = PermissionManager.INSTANCE.getPermission((int) permCode);
                    if (isCodeExist) {
                        var randomKey = Util.randomStringGen(32);
                        encryptedConnector.send(
                                Response.serialize(new Response().withResponseCode(Result.OK)
                                        .withContentPair("key",randomKey)
                                        .withContentPair("serverName", Objects.requireNonNull(GlobalVariable.INSTANCE.getConfig()).getServerName()))
                        );
                        logger.info(String.format("Starting SessionServer for #%s:%d", socket.getInetAddress(), socket.getPort()));
                        logger.debug(String.format("Key of %s:%d is %s", socket.getInetAddress(), socket.getPort(), randomKey));
                        var session = new SessionServer(new Session(socket, randomKey.getBytes(StandardCharsets.UTF_8)),permissions);
                        session.start();
                        break;
                    }else {
                        encryptedConnector.send(
                                Response.serialize(new Response().withResponseCode(Result.PERMISSION_DENIED))
                        );
                    }
                    break;
                }
                line = encryptedConnector.readLine();
            }
        } catch (Throwable e) {
            logger.error("Error occurred while handling Session login request: %s".formatted(e.toString()));
            logger.debug("Exception: ",e);
            try {
                socket.close();
            } catch (IOException ex) {
                logger.error("Error occurred while closing crashed session: %s".formatted(ex.toString()));
            }
        }
    }
}
