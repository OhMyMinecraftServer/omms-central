package icu.takeneko.omms.central.network.session.handler.builtin.permission;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.FailureReasons;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Operation;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.permission.PermissionChange;
import icu.takeneko.omms.central.permission.PermissionManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreatePermissionRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        try {
            PermissionManager.INSTANCE.submitPermissionChanges(
                    new PermissionChange(
                            Operation.CREATE
                            , request.getContent("name")
                            , List.of()
                    )
            );
            return request.success();
        } catch (Throwable e) {
            return request.fail(FailureReasons.PERMISSION_CHANGE_EXISTS);
        }
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.PERMISSION_MODIFY;
    }
}
