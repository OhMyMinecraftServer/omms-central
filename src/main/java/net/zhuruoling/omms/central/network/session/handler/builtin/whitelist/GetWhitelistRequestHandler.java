package net.zhuruoling.omms.central.network.session.handler.builtin.whitelist;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.util.Result;
import net.zhuruoling.omms.central.util.Util;
import net.zhuruoling.omms.central.whitelist.WhitelistManager;

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
