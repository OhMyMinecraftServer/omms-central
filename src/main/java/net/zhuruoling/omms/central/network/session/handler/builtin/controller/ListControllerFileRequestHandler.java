package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import org.jetbrains.annotations.Nullable;

public class ListControllerFileRequestHandler extends BuiltinRequestHandler {
    @Override
    public @Nullable Response handle(Request request, SessionContext session) {
        return null;
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return null;
    }
}