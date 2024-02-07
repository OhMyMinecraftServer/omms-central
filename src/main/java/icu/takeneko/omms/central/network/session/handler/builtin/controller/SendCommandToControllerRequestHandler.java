package icu.takeneko.omms.central.network.session.handler.builtin.controller;

import icu.takeneko.omms.central.controller.ControllerManager;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.controller.ControllerManager;
import icu.takeneko.omms.central.network.http.UtilKt;
import icu.takeneko.omms.central.controller.RequestUnauthorisedException;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SendCommandToControllerRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        Response response = new Response();
        var name = request.getContent("controller");
        var command = request.getContent("command");
        var controller = ControllerManager.INSTANCE.getControllerByName(name);
        if (controller == null) {
            return response.withResponseCode(Result.CONTROLLER_NOT_EXIST).withContentPair("controllerId", name);
        }
        try {
            var result = ControllerManager.INSTANCE.sendCommand(controller.getName(), command);
            if (result.getStatus()) {
                response.withResponseCode(Result.CONTROLLER_COMMAND_SENT)
                        .withContentPair("controllerId", name)
                        .withContentPair("status", String.valueOf(result.getStatus()))
                        .withContentPair("output", UtilKt.joinToString(result.getResult()));
            }else {
                response.withResponseCode(Result.CONTROLLER_COMMAND_SENT)
                        .withContentPair("controllerId", name)
                        .withContentPair("status", String.valueOf(result.getStatus()))
                        .withContentPair("output", result.getExceptionMessage())
                        .withContentPair("errorDetail", result.getExceptionDetail());
            }
            return response;
        } catch (RequestUnauthorisedException e) {
            return response.withResponseCode(Result.CONTROLLER_AUTH_FAILED).withContentPair("controllerId", name);
        }

    }

    @Override
    public @Nullable Permission requiresPermission() {
        return null;
    }
}
