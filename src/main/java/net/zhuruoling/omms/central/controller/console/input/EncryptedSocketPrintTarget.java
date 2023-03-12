package net.zhuruoling.omms.central.controller.console.input;

import net.zhuruoling.omms.central.controller.console.ControllerConsole;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.server.SessionServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptedSocketPrintTarget extends PrintTarget<SessionServer, ControllerConsole> {
    private final Logger logger = LoggerFactory.getLogger("EncryptedSocketPrintTarget");

    public EncryptedSocketPrintTarget(SessionServer target) {
        super(target);
    }


    Response responseBuilder(String content, String id){
        return new Response().withResponseCode(Result.CONTROLLER_LOG).withContentPair("consoleId", id).withContentPair("content",content);
    }

    @Override
    void println(SessionServer target, ControllerConsole console, String content) {
        try {
            logger.info(content);
            target.sendResponseAsync(responseBuilder(content, console.getConsoleId()));
        }catch (Exception e){
            throw new RuntimeException("Error occurred while sending log to client.",e);
        }
    }
}
