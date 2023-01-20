package net.zhuruoling.omms.central.network.session.server;

import net.zhuruoling.omms.central.network.EncryptedSocket;
import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.Session;
import net.zhuruoling.omms.central.network.session.request.RequestBuilderKt;
import net.zhuruoling.omms.central.network.session.request.RequestManager;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.network.session.response.Result;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class SessionServer extends Thread {
    private final Session session;
    private @Nullable EncryptedSocket encryptedConnector = null;
    final Logger logger = LoggerFactory.getLogger("SessionServer");
    List<Permission> permissions;
    public SessionServer(Session session, List<Permission> permissions){
        this.session = session;
        this.permissions = permissions;
        var socket = this.session.getSocket();
        this.setName(String.format("SessionServer#%s:%d",socket.getInetAddress().getHostAddress(), socket.getPort()));
        try {
            this.encryptedConnector = new EncryptedSocket(
                    new BufferedReader(
                            new InputStreamReader(this.session.getSocket().getInputStream())
                    ),
                    new PrintWriter(
                            new OutputStreamWriter(this.session.getSocket().getOutputStream())
                    ),
                    new String(
                            this.session.getKey()
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
                    if (session.getSocket().isClosed())
                        break;
                    var request = RequestBuilderKt.buildFromJson(line);
                    logger.info("Received " + request);
                    var handler = Objects.requireNonNull(RequestManager.INSTANCE.getRequestHandler(Objects.requireNonNull(request).getRequest()));
                    var permission = handler.requiresPermission();
                    if (permission != null && !permissions.contains(permission)) {
                        String response = Response.serialize(new Response().withResponseCode(Result.PERMISSION_DENIED));
                        encryptedConnector.println(response);
                        continue;
                    }
                    Response response;
                    try {
                        response = handler.handle(request, new HandlerSession(encryptedConnector, session, this.permissions));
                        if (response == null){
                            encryptedConnector.println(Response.serialize(new Response()));
                            logger.info("Disconnecting.");
                            session.getSocket().close();
                            break;
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                        response = new Response().withResponseCode(Result.FAIL).withContentPair("error", t.toString());
                    }

                    var content = Response.serialize(response);
                    encryptedConnector.println(content);
                    if (session.getSocket().isClosed())
                        break;
                    line = encryptedConnector.readLine();
                }
                catch (NullPointerException e){
                    break;
                }
                catch (Exception e) {

                    e.printStackTrace();
                    break;
                }
            }
            logger.info("Disconnecting.");
        } catch (Throwable e) {
            new RuntimeException(e).printStackTrace();
        }
    }

}
