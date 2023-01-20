package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.network.session.response.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SendCommandToControllerRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, HandlerSession session) {
        Response response = new Response();
        var name = request.getContent("controller");
        var command  = request.getContent("command");
        var controller = ControllerManager.INSTANCE.getControllerByName(name);
        if (controller == null){
            return response.withResponseCode(Result.CONTROLLER_NOT_EXIST);
        }
        var result = ControllerManager.INSTANCE.sendCommand(controller,command);
        response.withResponseCode(Result.OK).withContentPair("output", result == null ? "" : result);
        return response;
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return null;
    }
}
