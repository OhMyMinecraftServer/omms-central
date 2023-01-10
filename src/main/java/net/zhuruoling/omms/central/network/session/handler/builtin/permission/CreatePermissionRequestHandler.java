package net.zhuruoling.omms.central.network.session.handler.builtin.permission;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.permission.PermissionChange;
import net.zhuruoling.omms.central.permission.PermissionManager;
import net.zhuruoling.omms.central.util.Result;

public class CreatePermissionRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(Request request, HandlerSession session) {
        var response = new Response();
        try {
            PermissionManager.INSTANCE.submitPermissionChanges(
                    new PermissionChange(
                            PermissionChange.Operation.CREATE
                            , Integer.parseInt(request.getContent("code"))
                            , null
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
