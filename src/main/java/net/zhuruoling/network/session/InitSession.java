package net.zhuruoling.network.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.network.session.server.SessionServer;
import net.zhuruoling.network.session.request.RequestBuilderKt;
import net.zhuruoling.message.MessageBuilderKt;
import net.zhuruoling.network.EncryptedConnector;
import net.zhuruoling.permission.PermissionManager;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;
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
    private final EncryptedConnector encryptedConnector;
    private final Logger logger = LoggerFactory.getLogger("client-handler");
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final Socket socket;
    public InitSession(Socket socket) throws IOException {
        super(String.format("InitSession#%s:%d",socket.getInetAddress(), socket.getPort()));
        var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        var out = new PrintWriter(socket.getOutputStream(), true);
        var date = LocalDateTime.now();
        var key = date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"));
        key = Util.base64Encode(Util.base64Encode(key));
        logger.debug("key:%s".formatted(key));
        this.encryptedConnector = new EncryptedConnector(in, out, key);
        logger.info("client:" + socket.getInetAddress() + ":" + socket.getPort() + " connected.");
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String line = encryptedConnector.readLine();
            logger.debug("recv:" + line);
            while (true){
                var request = RequestBuilderKt.buildFromJson(line);
                logger.info(String.valueOf(request));
                if (Objects.equals(Objects.requireNonNull(request).getRequest(), "PING")) {
                    var authKey = new String(Base64.getDecoder().decode(Base64.getDecoder().decode(request.getLoad()[0])));
                    //202205290840#114514
                    var date = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm")));
                    var key = Long.parseLong(authKey);
                    long permCode = key ^ date;
                    logger.debug("Got permission code %d".formatted(permCode));
                    boolean isCodeExist = !(PermissionManager.INSTANCE.getPermission(
                            (int) permCode
                    ) == null);
                    var permissions = PermissionManager.INSTANCE.getPermission((int) permCode);
                    if (isCodeExist) {
                        var randomKey = Util.randomStringGen(32);
                        encryptedConnector.send(
                                MessageBuilderKt.build(
                                        Result.OK,
                                        new String[]{randomKey}
                                )
                        );
                        logger.info(String.format("Starting Session for #%s:%d", socket.getInetAddress(), socket.getPort()));
                        logger.info(String.format("Key of %s:%d is %s", socket.getInetAddress(), socket.getPort(), randomKey));
                        var session = new SessionServer(new Session(socket, randomKey.getBytes(StandardCharsets.UTF_8)),permissions);
                        session.start();
                        break;
                    }
                    encryptedConnector.send(
                            MessageBuilderKt.build(Result.FAIL)
                    );
                    break;
                }
                line = encryptedConnector.readLine();
            }
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
