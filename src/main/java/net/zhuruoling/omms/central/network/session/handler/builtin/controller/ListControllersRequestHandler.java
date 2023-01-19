package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.util.Util;

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