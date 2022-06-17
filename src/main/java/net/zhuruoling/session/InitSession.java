package net.zhuruoling.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.EncryptedConnector;
import net.zhuruoling.command.CommandBuilderKt;
import net.zhuruoling.message.MessageBuilderKt;
import net.zhuruoling.permcode.PermissionManager;
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
        this.encryptedConnector = new EncryptedConnector(in, out, key);
        logger.info("client:" + socket.getInetAddress() + ":" + socket.getPort() + " connected.");
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String line = encryptedConnector.readLine();
            while (true){
                var command = CommandBuilderKt.buildFromJson(line);
                if (Objects.equals(Objects.requireNonNull(command).getCmd(), "PING")) {
                    var authKey = new String(Base64.getDecoder().decode(Base64.getDecoder().decode(command.getLoad()[0])));
                    //202205290840#114514
                    var date = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm")));
                    var key = Integer.parseInt(authKey);
                    var permCode = key ^ date;

                    boolean isCodeExist = !(PermissionManager.INSTANCE.getPermission(
                            permCode
                    ) == null);

                    if (isCodeExist) {
                        var randomKey = Util.randomStringGen(32);
                        encryptedConnector.send(
                                MessageBuilderKt.build(
                                        Result.OK,
                                        new String[]{randomKey}
                                )
                        );
                        logger.info(String.format("Starting Session for #%s:%d", socket.getInetAddress(), socket.getPort()));
                        logger.info(String.format("Key of #%s:%d is %s", socket.getInetAddress(), socket.getPort(), randomKey));
                        var session = new SessionServer(new Session(socket, randomKey.getBytes(StandardCharsets.UTF_8)));
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
            this.socket.close();
            return;
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
