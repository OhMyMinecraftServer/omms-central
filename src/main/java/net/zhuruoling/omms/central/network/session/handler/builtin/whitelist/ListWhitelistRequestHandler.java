package net.zhuruoling.omms.central.network.session.handler.builtin.whitelist;

import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.util.Util;
import net.zhuruoling.omms.central.whitelist.WhitelistManager;
import org.jetbrains.annotations.Nullable;

public class ListWhitelistRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(Request request, SessionContext session) {
        return new Response().withResponseCode(Result.WHITELIST_LISTED)
                .withContentPair(
                        "whitelists",
                        Util.gson.toJson(
                                WhitelistManager.INSTANCE.getWhitelistNames()
                        )
                );
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return null;
    }
}
