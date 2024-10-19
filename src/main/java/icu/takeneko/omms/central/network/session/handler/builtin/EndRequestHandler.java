package icu.takeneko.omms.central.network.session.handler.builtin;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import org.jetbrains.annotations.Nullable;

public class EndRequestHandler extends BuiltinRequestHandler {

    @Override
    public @Nullable Response handle(Request request, SessionContext session) {
        return null;//do nothing
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return null; //does not require any permission
    }
}