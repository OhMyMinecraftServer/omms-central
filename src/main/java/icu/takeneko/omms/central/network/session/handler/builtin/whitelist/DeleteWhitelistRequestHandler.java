package icu.takeneko.omms.central.network.session.handler.builtin.whitelist;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.FailureReasons;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.whitelist.WhitelistManager;
import icu.takeneko.omms.central.whitelist.WhitelistNotExistException;
import org.jetbrains.annotations.NotNull;

public class DeleteWhitelistRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        try {
            WhitelistManager.INSTANCE.deleteWhiteList(request.getContent("whitelist"));
            return request.success();
        } catch (WhitelistNotExistException e) {
            return request.fail(FailureReasons.WHITELIST_NOT_FOUND);
        }
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.WHITELIST_DELETE;
    }
}
