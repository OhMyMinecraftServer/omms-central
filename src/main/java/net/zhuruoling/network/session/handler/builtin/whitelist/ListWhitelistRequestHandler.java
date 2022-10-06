package net.zhuruoling.network.session.handler.builtin.whitelist;

import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;
import net.zhuruoling.whitelist.WhitelistManager;

public class ListWhitelistRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(Request request, HandlerSession session) {
        return new Response().withResponseCode(Result.OK)
                .withContentPair(
                        "whitelists",
                        Util.gson.toJson(
                                WhitelistManager.INSTANCE.getWhitelistNames()
                        )
                );
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}
