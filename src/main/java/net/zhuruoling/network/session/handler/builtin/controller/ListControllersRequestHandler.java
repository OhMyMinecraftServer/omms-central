package net.zhuruoling.network.session.handler.builtin.controller;

import net.zhuruoling.controller.ControllerManager;
import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;

public class ListControllersRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        var controllerNames = ControllerManager.INSTANCE.getControllers().keySet();
        var json = Util.gson.toJson(controllerNames);
        return new Response().withResponseCode(Result.OK).withContentPair("names", json);
    }

    @Override
    public Permission requiresPermission() {
        return Permission.CONTROLLER_GET;
    }
}
