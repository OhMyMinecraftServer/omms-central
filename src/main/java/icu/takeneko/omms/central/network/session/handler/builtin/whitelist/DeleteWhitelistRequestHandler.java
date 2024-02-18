package icu.takeneko.omms.central.network.session.handler.builtin.whitelist;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.whitelist.WhitelistManager;
import org.jetbrains.annotations.NotNull;

public class DeleteWhitelistRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        Result result = Result.FAIL;
        try {
            WhitelistManager.INSTANCE.deleteWhiteList(request.getContent("whitelist"));
            result = Result.WHITELIST_DELETED;
        } catch (Exception e) {

        }
        return new Response().withResponseCode(result).withContentPair("whitelist", request.getContent("whitelist"));
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.WHITELIST_DELETE;
    }
}
