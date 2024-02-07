package icu.takeneko.omms.central.network.session.handler.builtin.permission;

import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.permission.PermissionManager;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.permission.Operation;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.permission.PermissionChange;
import icu.takeneko.omms.central.permission.PermissionManager;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GrantPermissionRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        var response = new Response();
        try {
            PermissionManager.INSTANCE.submitPermissionChanges(
                    new PermissionChange(
                            Operation.GRANT
                            , Integer.parseInt(request.getContent("code"))
                            , Arrays.asList(Util.gson.fromJson(request.getContent("permissions"), Permission[].class))
                    )
            );
            return response.withResponseCode(Result.PERMISSION_GRANTED);
        }
        catch (Throwable e){
            return response.withResponseCode(Result.OPERATION_ALREADY_EXISTS);
        }
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.PERMISSION_MODIFY;
    }
}
