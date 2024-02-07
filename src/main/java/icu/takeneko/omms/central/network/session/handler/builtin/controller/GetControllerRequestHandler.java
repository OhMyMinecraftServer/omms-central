package icu.takeneko.omms.central.network.session.handler.builtin.controller;

import icu.takeneko.omms.central.controller.ControllerManager;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.ControllerData;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.controller.ControllerManager;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.ControllerData;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

public class GetControllerRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        String name = request.getContent("controller");
        var controller = ControllerManager.INSTANCE.getControllerByName(name);
        if (controller == null){
            return new Response().withResponseCode(Result.CONTROLLER_NOT_EXIST).withContentPair("controllerId", name);
        }
        return new Response().withResponseCode(Result.CONTROLLER_GOT).withContentPair("controller", Util.toJson(ControllerData.fromController(controller)));
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}
