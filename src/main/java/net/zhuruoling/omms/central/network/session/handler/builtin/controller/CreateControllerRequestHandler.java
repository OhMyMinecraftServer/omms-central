package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.util.Result;

public class CreateControllerRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        return new Response().withResponseCode(Result.OK);
    }

    @Override
    public Permission requiresPermission() {
        return Permission.CONTROLLER_MODIFY;
    }
}
