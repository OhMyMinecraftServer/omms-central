package icu.takeneko.omms.central.network.session.handler.builtin.whitelist;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.FailureReasons;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.whitelist.WhitelistAlreadyExistsException;
import icu.takeneko.omms.central.whitelist.WhitelistManager;
import org.jetbrains.annotations.NotNull;

public class CreateWhitelistRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        try {
            WhitelistManager.INSTANCE.createWhitelist(request.getContent("whitelist"));
            return request.success();
        } catch (WhitelistAlreadyExistsException e) {
            return request.fail(FailureReasons.WHITELIST_EXISTS);
        }
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.WHITELIST_CREATE;
    }
}
