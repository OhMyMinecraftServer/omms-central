package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.network.old.session.SessionContext;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

public class GetControllerStatusRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        var controllerId = request.getContent("id");
        if (ControllerManager.INSTANCE.getControllers().containsKey(controllerId)) {
            var status = ControllerManager.INSTANCE.getControllerStatus(kotlin.collections.CollectionsKt.mutableListOf(controllerId));
            if (status.containsKey(controllerId)){
                return new Response().withResponseCode(Result.CONTROLLER_STATUS_GOT).withContentPair("status", Util.toJson(status.get(controllerId)));
            }else {
                return new Response().withResponseCode(Result.CONTROLLER_NO_STATUS);
            }
        } else {
            return new Response().withResponseCode(Result.CONTROLLER_NOT_EXIST).withContentPair("controllerId", controllerId);
        }
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}
