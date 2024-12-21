package icu.takeneko.omms.central.controller.console.output;

import icu.takeneko.omms.central.controller.console.ControllerConsole;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Status;
import icu.takeneko.omms.central.network.session.server.SessionServer;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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
        String requestId = this.target.sessionContext.getControllerConsoleRequestIds().get(id);
        return new Response(requestId, Status.SUCCESS, Map.of(
            "content", content,
            "marker_log","",
            "consoleId",id
        ));
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
