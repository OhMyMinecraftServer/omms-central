package icu.takeneko.omms.central.controller.console.output;

import icu.takeneko.omms.central.controller.console.ControllerConsole;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.network.session.server.SessionServer;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EncryptedSocketPrintTarget extends PrintTarget<SessionServer, ControllerConsole> {
    private final Logger logger = LoggerFactory.getLogger("EncryptedSocketPrintTarget");

    public EncryptedSocketPrintTarget(SessionServer target) {
        super(target);
    }

    @Override
    public void printMultiLine(List<String> content, ControllerConsole ctx) {
        println(Util.joinToString(content, "\n"), ctx);
    }

    @NotNull
    Response buildResponse(String content, String id) {
        return new Response().withResponseCode(Result.CONTROLLER_LOG).withContentPair("consoleId", id).withContentPair("content", content);
    }

    @Override
    void println(@NotNull SessionServer target, @NotNull ControllerConsole console, String content) {
        try {
            logger.debug(content);
            target.sendResponseBlocking(buildResponse(content, console.getConsoleId()));
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while sending log to client.", e);
        }
    }
}
