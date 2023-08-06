package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.network.http.UtilKt;
import net.zhuruoling.omms.central.network.http.client.RequestUnauthorisedException;
import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SendCommandToControllerRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        Response response = new Response();
        var name = request.getContent("controller");
        var command  = request.getContent("command");
        var controller = ControllerManager.INSTANCE.getControllerByName(name);
        if (controller == null){
            return response.withResponseCode(Result.CONTROLLER_NOT_EXIST).withContentPair("controllerId", name);
        }
        try {
            var result = ControllerManager.INSTANCE.sendCommand(controller.getName(), command);
            response.withResponseCode(Result.CONTROLLER_COMMAND_SENT).withContentPair("controllerId", name).withContentPair("output", UtilKt.joinToString(result));
            return response;
        }catch (RequestUnauthorisedException e){
            return response.withResponseCode(Result.CONTROLLER_AUTH_FAILED).withContentPair("controllerId", name);
        }

    }

    @Override
    public @Nullable Permission requiresPermission() {
        return null;
    }
}
