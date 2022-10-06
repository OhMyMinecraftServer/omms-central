package net.zhuruoling.network.session.handler.builtin.controller;

import net.zhuruoling.controller.ControllerManager;
import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.util.Result;

public class GetControllersRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(Request request, HandlerSession session) {
        String name = request.getContent("controller");
        var controller = ControllerManager.INSTANCE.getControllerByName(name);
        if (controller == null){
            return new Response().withResponseCode(Result.CONTROLLER_NOT_EXIST);
        }
        return new Response().withContentPair("controller", controller.toJson());
    }

    @Override
    public Permission requiresPermission() {
        return Permission.CONTROLLER_GET;
    }
}
