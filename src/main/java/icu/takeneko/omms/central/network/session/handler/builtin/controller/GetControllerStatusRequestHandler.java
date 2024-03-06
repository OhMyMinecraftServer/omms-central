package icu.takeneko.omms.central.network.session.handler.builtin.controller;

import icu.takeneko.omms.central.controller.ControllerManager;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

public class GetControllerStatusRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        var controllerId = request.getContent("id");
        if (ControllerManager.INSTANCE.getControllers().containsKey(controllerId)) {
            var status = ControllerManager.INSTANCE.getControllerStatus(kotlin.collections.CollectionsKt.mutableListOf(controllerId));
            return new Response().withResponseCode(Result.CONTROLLER_STATUS_GOT).withContentPair("status", Util.toJson(status.get(controllerId)));
        } else {
            return new Response().withResponseCode(Result.CONTROLLER_NOT_EXIST).withContentPair("controllerId", controllerId);
        }
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}
