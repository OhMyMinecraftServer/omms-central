package icu.takeneko.omms.central.network.session.handler.builtin.whitelist;

import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.util.Util;
import icu.takeneko.omms.central.whitelist.WhitelistManager;
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
