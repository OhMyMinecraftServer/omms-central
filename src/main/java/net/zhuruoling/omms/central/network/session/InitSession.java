package net.zhuruoling.omms.central.network.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.omms.central.network.EncryptedSocket;
import net.zhuruoling.omms.central.network.session.request.InitRequest;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.server.SessionServer;
import net.zhuruoling.omms.central.permission.PermissionManager;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;

public class InitSession extends Thread {
    private final @NotNull EncryptedSocket encryptedConnector;
    private final Logger logger = LoggerFactory.getLogger("InitSession");
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final @NotNull Socket socket;
    public InitSession(@NotNull Socket socket) throws IOException {
        super(String.format("InitSession#%s:%d",socket.getInetAddress(), socket.getPort()));
        var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        var out = new PrintWriter(socket.getOutputStream(), true);
        var date = LocalDateTime.now();
        var key = date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"));
        key = Util.base64Encode(Util.base64Encode(key));
        logger.debug("key:%s".formatted(key));
        this.encryptedConnector = new EncryptedSocket(in, out, key);
        logger.info("client:" + socket.getInetAddress() + ":" + socket.getPort() + " connected.");
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String line = encryptedConnector.readLine();
            while (true){
                var request = gson.fromJson(line, InitRequest.class);
                logger.debug("Got request:" + request);
                if (Objects.equals(Objects.requireNonNull(request).getRequest(), "PING")) {
                    //lets match versions.
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
                                Response.serialize(new Response().withResponseCode(Result.OK).withContentPair("key",randomKey))
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
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
