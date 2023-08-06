package net.zhuruoling.omms.central.network.session.handler.builtin.whitelist;

import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.whitelist.WhitelistAlreadyExistsException;
import net.zhuruoling.omms.central.whitelist.WhitelistManager;
import org.jetbrains.annotations.NotNull;

public class CreateWhitelistRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        Result result;
        try {
            WhitelistManager.INSTANCE.createWhitelist(request.getContent("whitelist"));
            result = Result.WHITELIST_CREATED;
        } catch (WhitelistAlreadyExistsException e) {
            result = Result.WHITELIST_ALREADT_EXISTS;
        }
        return new Response().withResponseCode(result).withContentPair("whitelist", request.getContent("whitelist"));
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.WHITELIST_CREATE;
    }
}
