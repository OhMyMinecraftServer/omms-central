package icu.takeneko.omms.central.network.session.handler.builtin.whitelist;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.FailureReasons;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.whitelist.PlayerNotFoundException;
import icu.takeneko.omms.central.whitelist.WhitelistManager;
import icu.takeneko.omms.central.whitelist.WhitelistNotExistException;
import org.jetbrains.annotations.NotNull;

public class RemoveFromWhitelistHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        try {
            WhitelistManager.INSTANCE.removeFromWhiteList(
                    request.getContent("whitelist"),
                    request.getContent("player")
            );
            return request.success();
        } catch (WhitelistNotExistException e) {
            return request.fail(FailureReasons.WHITELIST_NOT_FOUND);
        } catch (PlayerNotFoundException e) {
            return request.fail(FailureReasons.PLAYER_NOT_FOUND);
        }
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.WHITELIST_REMOVE;
    }
}