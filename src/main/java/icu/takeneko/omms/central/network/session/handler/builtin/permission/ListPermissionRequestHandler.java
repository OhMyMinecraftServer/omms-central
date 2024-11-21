package icu.takeneko.omms.central.network.session.handler.builtin.permission;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Status;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.permission.PermissionManager;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

public class ListPermissionRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, SessionContext session) {
        var codes = PermissionManager.INSTANCE.getPermissionTable().keySet();
        var codeStrings = Util.gson.toJson(codes);
        return request.success().withContentPair("codes", codeStrings);
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.PERMISSION_LIST;
    }
}