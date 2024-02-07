package icu.takeneko.omms.central.network.session.handler.builtin.controller;

import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EndControllerConsoleRequestHandler extends BuiltinRequestHandler {
    @Override
    public @Nullable Response handle(@NotNull Request request, @NotNull SessionContext session) {
        String id = request.getContent("consoleId");
        if (session.getControllerConsoleMap().containsKey(id)){
            var console = session.getControllerConsoleMap().get(id);
            console.close();
            session.getControllerConsoleMap().remove(id);
            return new Response().withResponseCode(Result.CONSOLE_STOPPED).withContentPair("consoleId",id);
        }else {
            return new Response().withResponseCode(Result.CONSOLE_NOT_EXIST).withContentPair("consoleId",id);
        }
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return Permission.CONTROLLER_CONTROL;
    }
}
