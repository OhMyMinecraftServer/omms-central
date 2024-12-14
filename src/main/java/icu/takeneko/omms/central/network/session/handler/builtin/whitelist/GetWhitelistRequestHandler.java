package icu.takeneko.omms.central.network.session.handler.builtin.whitelist;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.FailureReasons;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.util.Util;
import icu.takeneko.omms.central.whitelist.WhitelistManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GetWhitelistRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        var whitelist = request.getContent("whitelist");
        if (!WhitelistManager.INSTANCE.hasWhitelist(whitelist)) {
            return request.fail(FailureReasons.WHITELIST_NOT_FOUND);
        }
        return request.success()
            .withContentPair("whitelist", whitelist)
            .withContentPair(
                "players",
                Util.gson.toJson(
                    Objects.requireNonNull(WhitelistManager.INSTANCE.getWhitelist(whitelist)).getPlayers()
                )
            );
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return null;
    }
}
