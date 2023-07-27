package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.controller.console.input.SessionInputSource;
import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SendControllerConsoleInputRequestHandler extends BuiltinRequestHandler {
    @Override
    public @Nullable Response handle(@NotNull Request request, @NotNull SessionContext session) {//NOTE: may ignore by client
        String id = request.getContent("consoleId");
        if (session.getControllerConsoleMap().containsKey(id)) {
            var console = session.getControllerConsoleMap().get(id);
            String line = request.getContent("command");
            var inputSource = (SessionInputSource) console.getInputSource();
            inputSource.put(line);
            return new Response().withResponseCode(Result.CONTROLLER_CONSOLE_INPUT_SENT);
        } else {
            return new Response().withResponseCode(Result.CONSOLE_NOT_EXIST);
        }
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return Permission.CONTROLLER_CONTROL;
    }
}
