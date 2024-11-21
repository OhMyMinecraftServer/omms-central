package icu.takeneko.omms.central.network.session.handler.builtin.controller;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
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
