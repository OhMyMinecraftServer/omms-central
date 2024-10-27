package icu.takeneko.omms.central.network.session.handler.builtin.controller;

import icu.takeneko.omms.central.controller.console.ControllerConsole;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.Nullable;

public class RequestCommandCompletionRequestHandler extends BuiltinRequestHandler {

    @Override
    public @Nullable Response handle(Request request, SessionContext session) {
        String id = request.getContent("consoleId");
        if (session.getControllerConsoleMap().containsKey(id)) {
            ControllerConsole console = session.getControllerConsoleMap().get(id);
            String line = request.getContent("input");
            String completionId = Util.generateRandomString(8);
            int cursorPosition = Integer.parseInt(request.getContent("cursor"));
            console.complete(line, cursorPosition)
                .thenAccept(it -> session.getServer().sendCompletionResult(it, completionId));
            return new Response()
                .withResponseCode(Result.CONTROLLER_CONSOLE_COMPLETION_SENT)
                .withContentPair("completionId", completionId);
        } else {
            return new Response().withResponseCode(Result.CONSOLE_NOT_EXIST);
        }
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return null;
    }
}
