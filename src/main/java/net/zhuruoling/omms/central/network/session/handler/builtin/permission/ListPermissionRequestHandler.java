package net.zhuruoling.omms.central.network.session.handler.builtin.permission;

import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.permission.PermissionManager;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

public class ListPermissionRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, SessionContext session) {
        var codes = PermissionManager.INSTANCE.getPermissionTable().keySet();
        var codeStrings = Util.gson.toJson(codes);
        return new Response().withContentPair("codes",codeStrings).withResponseCode(Result.PERMISSION_LISTED);
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.PERMISSION_LIST;
    }
}