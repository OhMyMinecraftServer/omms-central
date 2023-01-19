package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.util.Util;

public class GetControllerStatusRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        var controllerId = request.getContent("id");
        if (ControllerManager.INSTANCE.getControllers().containsKey(controllerId)) {
            var status = ControllerManager.INSTANCE.getControllerStatus(kotlin.collections.CollectionsKt.mutableListOf(controllerId));
            if (status.containsKey(controllerId)){
                return new Response().withResponseCode(Result.OK).withContentPair("status", Util.toJson(status.get(controllerId)));
            }else {
                return new Response().withResponseCode(Result.CONTROLLER_NO_STATUS);
            }
        } else {
            return new Response().withResponseCode(Result.CONTROLLER_NOT_EXIST);
        }
    }

    @Override
    public Permission requiresPermission() {
        return Permission.CONTROLLER_EXECUTE;
    }
}
