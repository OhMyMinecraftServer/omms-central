package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import org.jetbrains.annotations.NotNull;

public class CreateControllerRequestHandler extends BuiltinRequestHandler {//todo
    @Override
    public Response handle(Request request, SessionContext session) {
        return null;
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.CONTROLLER_CREATE;
    }
}
