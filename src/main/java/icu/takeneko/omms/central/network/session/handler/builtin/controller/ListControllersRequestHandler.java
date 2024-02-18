package icu.takeneko.omms.central.network.session.handler.builtin.controller;

import icu.takeneko.omms.central.controller.ControllerManager;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.util.Util;

public class ListControllersRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, SessionContext session) {
        var controllerNames = ControllerManager.INSTANCE.getControllers().keySet();
        var json = Util.gson.toJson(controllerNames);
        return new Response().withResponseCode(Result.CONTROLLER_LISTED).withContentPair("names", json);
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}