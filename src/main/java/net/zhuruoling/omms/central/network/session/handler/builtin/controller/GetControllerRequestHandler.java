package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.network.old.session.SessionContext;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.ControllerData;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.util.Util;
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
