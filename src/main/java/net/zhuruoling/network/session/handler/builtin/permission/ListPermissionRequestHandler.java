package net.zhuruoling.network.session.handler.builtin.permission;

import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.permission.PermissionManager;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;

import java.util.ArrayList;

public class ListPermissionRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        var codes = PermissionManager.INSTANCE.getPermissionTable().keySet();
        var codeStrings = Util.gson.toJson(codes);
        return new Response().withContentPair("codes",codeStrings).withResponseCode(Result.OK);
    }

    @Override
    public Permission requiresPermission() {
        return Permission.PERMISSION_LIST;
    }
}
