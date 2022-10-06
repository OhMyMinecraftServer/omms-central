package net.zhuruoling.network.session.handler.builtin.whitelist;

import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.whitelist.WhitelistManager;

public class DeleteWhitelistRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        return new Response().withResponseCode(
                WhitelistManager.INSTANCE.deleteWhiteList(
                        request.getContent("whitelist")
                )
        );
    }

    @Override
    public Permission requiresPermission() {
        return Permission.WHITELIST_DELETE;
    }
}
