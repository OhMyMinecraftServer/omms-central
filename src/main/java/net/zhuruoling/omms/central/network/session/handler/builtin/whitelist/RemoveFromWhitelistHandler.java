package net.zhuruoling.omms.central.network.session.handler.builtin.whitelist;

import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.whitelist.PlayerAlreadyExistsException;
import net.zhuruoling.omms.central.whitelist.PlayerNotFoundException;
import net.zhuruoling.omms.central.whitelist.WhitelistManager;
import net.zhuruoling.omms.central.whitelist.WhitelistNotExistException;
import org.jetbrains.annotations.NotNull;

public class RemoveFromWhitelistHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        var result = Result.WHITELIST_REMOVED;
        try {
            WhitelistManager.INSTANCE.removeFromWhiteList(
                    request.getContent("whitelist"),
                    request.getContent("player")
            );
        } catch (WhitelistNotExistException e) {
            result = Result.WHITELIST_NOT_EXIST;
        } catch (PlayerNotFoundException e) {
            result = Result.PLAYER_NOT_EXIST;
        }
        return new Response().withResponseCode(
                result
        ).withContentPair("whitelist", request.getContent("whitelist")).withContentPair("player", request.getContent("player"));
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.WHITELIST_REMOVE;
    }
}