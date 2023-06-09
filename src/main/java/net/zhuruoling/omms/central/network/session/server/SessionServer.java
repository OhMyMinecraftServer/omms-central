package net.zhuruoling.omms.central.network.session.server;

import net.zhuruoling.omms.central.GlobalVariable;
import net.zhuruoling.omms.central.network.EncryptedSocket;
import net.zhuruoling.omms.central.network.session.RateExceedException;
import net.zhuruoling.omms.central.network.session.FuseEncryptedSocket;
import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.Session;
import net.zhuruoling.omms.central.network.session.request.RequestManager;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.network.session.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionServer extends Thread {
    private final Session session;
    private FuseEncryptedSocket fuseEncryptedSocket;
    final Logger logger = LoggerFactory.getLogger("SessionServer");
    private SessionContext sessionContext;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
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
            this.fuseEncryptedSocket = FuseEncryptedSocket.of(encryptedConnector, GlobalVariable.INSTANCE.getConfig().getRateLimit());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (socket.isClosed()){
            throw new RuntimeException();
        }
    }

    private void runOnNetworkThread(Runnable runnable){
        this.executorService.submit(runnable);
    }

    public void sendResponseAsync(Response response){
        runOnNetworkThread(() -> {
            try {
                fuseEncryptedSocket.sendResponse(response);
            } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                     BadPaddingException | InvalidKeyException e) {
                logger.error("Error while sending response.", e);
                throw new RuntimeException(e);
            }
        });
    }
    private void cleanUp(){
        this.sessionContext.getControllerConsoleMap().forEach((s, controllerConsole) -> {
            logger.info("Closing controller console %s".formatted(s));
            controllerConsole.close();
        });
    }
    @Override
    public void run() {
        logger.info("%s started.".formatted(this.getName()));
        sessionContext = new SessionContext(this, fuseEncryptedSocket, session, this.permissions);
        try {
            while (true){
                try {
                    if (session.getSocket().isClosed())
                        break;
                    var request = fuseEncryptedSocket.receiveRequest();
                    logger.debug("Received " + request);
                    var handler = Objects.requireNonNull(RequestManager.INSTANCE.getRequestHandler(Objects.requireNonNull(request).getRequest()));
                    var permission = handler.requiresPermission();
                    if (permission != null && !permissions.contains(permission)) {
                        sendResponseAsync(new Response().withResponseCode(Result.PERMISSION_DENIED));
                        continue;
                    }
                    Response response;
                    try {
                        response = handler.handle(request, sessionContext);
                        if (response == null){
                            logger.info("Session terminated.");
                            fuseEncryptedSocket.sendResponse(new Response().withResponseCode(Result.DISCONNECT));
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
                    sendResponseAsync(response);
                }
                catch (NullPointerException e){
                    break;
                }
                catch (SocketException e){
                    logger.warn(e.toString());
                    break;
                }
                catch (RateExceedException e){
                    sendResponseAsync(new Response().withResponseCode(Result.RATE_LIMIT_EXCEEDED));
                    logger.warn("Rate limit exceeded.");
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
        cleanUp();
    }

}
