package icu.takeneko.omms.central.network.session.handler.builtin.controller;

import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
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
