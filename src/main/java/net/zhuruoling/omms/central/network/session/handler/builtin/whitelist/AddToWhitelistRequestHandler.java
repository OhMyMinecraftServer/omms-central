package net.zhuruoling.omms.central.network.session.handler.builtin.whitelist;

import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.whitelist.WhitelistManager;
import org.jetbrains.annotations.NotNull;

public class AddToWhitelistRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        var result = WhitelistManager.INSTANCE.addToWhiteList(
                request.getContent("whitelist"),
                request.getContent("player")
        );
        return new Response().withResponseCode(
               result
        ).withContentPair("whitelist", request.getContent("whitelist")).withContentPair("player", request.getContent("player"));
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.WHITELIST_ADD;
    }
}
