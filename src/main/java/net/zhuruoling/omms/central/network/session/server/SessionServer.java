package net.zhuruoling.omms.central.network.session.server;

import net.zhuruoling.omms.central.main.RuntimeConstants;
import net.zhuruoling.omms.central.network.EncryptedSocket;
import net.zhuruoling.omms.central.network.session.RateExceedException;
import net.zhuruoling.omms.central.network.session.RateLimitEncryptedSocket;
import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.Session;
import net.zhuruoling.omms.central.network.session.request.RequestBuilderKt;
import net.zhuruoling.omms.central.network.session.request.RequestManager;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.network.session.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class SessionServer extends Thread {
    private final Session session;
    private net.zhuruoling.omms.central.network.session.RateLimitEncryptedSocket rateLimitEncryptedSocket;
    final Logger logger = LoggerFactory.getLogger("SessionServer");
    List<Permission> permissions;
    public SessionServer(Session session, List<Permission> permissions){
        this.session = session;
        this.permissions = permissions;
        var socket = this.session.getSocket();
        this.setName(String.format("SessionServer#%s:%d",socket.getInetAddress().getHostAddress(), socket.getPort()));
        try {
             var encryptedConnector = new EncryptedSocket(
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
            this.rateLimitEncryptedSocket = RateLimitEncryptedSocket.of(encryptedConnector, RuntimeConstants.INSTANCE.getConfig().getPacketLimit());
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
        try {
            while (true){
                try {
                    if (session.getSocket().isClosed())
                        break;
                    var request = rateLimitEncryptedSocket.receiveRequest();
                    logger.debug("Received " + request);
                    var handler = Objects.requireNonNull(RequestManager.INSTANCE.getRequestHandler(Objects.requireNonNull(request).getRequest()));
                    var permission = handler.requiresPermission();
                    if (permission != null && !permissions.contains(permission)) {
                        rateLimitEncryptedSocket.sendResponse(new Response().withResponseCode(Result.PERMISSION_DENIED));
                        continue;
                    }
                    Response response;
                    try {
                        response = handler.handle(request, new SessionContext(rateLimitEncryptedSocket, session, this.permissions));
                        if (response == null){
                            logger.info("Session terminated.");
                            rateLimitEncryptedSocket.sendResponse(new Response());
                            session.getSocket().close();
                            break;
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                        response = new Response().withResponseCode(Result.FAIL).withContentPair("error", t.toString());
                    }

                    if (session.getSocket().isClosed()){
                        break;
                    }
                    rateLimitEncryptedSocket.sendResponse(response);
                }
                catch (NullPointerException e){
                    break;
                }
                catch (RateExceedException e){
                    rateLimitEncryptedSocket.sendResponse(new Response().withResponseCode(Result.RATE_LIMIT_EXCEEDED));
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
