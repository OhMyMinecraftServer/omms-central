package net.zhuruoling.omms.central.network.session.handler.builtin.whitelist;

import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.util.Util;
import net.zhuruoling.omms.central.whitelist.WhitelistManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GetWhitelistRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
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
    public @Nullable Permission requiresPermission() {
        return null;
    }
}
