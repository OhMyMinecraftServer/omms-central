package net.zhuruoling.network.session.handler.builtin.whitelist;

import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;
import net.zhuruoling.whitelist.WhitelistManager;

public class GetWhitelistRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        var whitelist = request.getContent("whitelist");
        return new Response().withResponseCode(Result.OK)
                .withContentPair("whitelist", whitelist)
                .withContentPair(
                        "players",
                        Util.gson.toJson(
                                WhitelistManager.INSTANCE.getWhitelist(whitelist).getPlayers()
                        )
                );
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}
