package net.zhuruoling.network.session.handler.builtin.permission;

import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.permission.PermissionChange;
import net.zhuruoling.permission.PermissionManager;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;

import java.util.Arrays;

public class DenyPermissionRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        var response = new Response();
        try {
            //草 彳亍
            //|ishland/
            PermissionManager.INSTANCE.submitPermissionChanges(
                    new PermissionChange(
                            PermissionChange.Operation.DENY
                            , Integer.parseInt(request.getContent("code"))
                            , Arrays.asList(Util.gson.fromJson(request.getContent("permissions"), Permission[].class))
                    )
            );
            return response.withResponseCode(Result.OK);
        }
        catch (Throwable e){
            return response.withResponseCode(Result.OPERATION_ALREADY_EXISTS);
        }
    }

    @Override
    public Permission requiresPermission() {
        return Permission.PERMISSION_MODIFY;
    }
}
