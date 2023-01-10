package net.zhuruoling.omms.central.network.session.handler.builtin.whitelist;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.whitelist.WhitelistManager;

public class RemoveFromWhitelistHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        return new Response().withResponseCode(
                WhitelistManager.INSTANCE.removeFromWhiteList(
                        request.getContent("whitelist"),
                        request.getContent("player")
                )
        );
    }

    @Override
    public Permission requiresPermission() {
        return Permission.WHITELIST_REMOVE;
    }
}
