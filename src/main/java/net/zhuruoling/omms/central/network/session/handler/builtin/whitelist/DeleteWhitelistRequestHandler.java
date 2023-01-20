package net.zhuruoling.omms.central.network.session.handler.builtin.whitelist;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.whitelist.WhitelistManager;
import org.jetbrains.annotations.NotNull;

public class DeleteWhitelistRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, HandlerSession session) {
        return new Response().withResponseCode(
                WhitelistManager.INSTANCE.deleteWhiteList(
                        request.getContent("whitelist")
                )
        );
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.WHITELIST_DELETE;
    }
}
