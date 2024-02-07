package icu.takeneko.omms.central.network.session.handler.builtin.announcement;

import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.announcement.AnnouncementManager;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GetAnnouncementRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        var announcement = AnnouncementManager.INSTANCE.get(request.getContent("id"));
        if (Objects.isNull(announcement)){
            return new Response().withResponseCode(Result.ANNOUNCEMENT_NOT_EXIST);
        }
        return new Response()
                .withResponseCode(Result.ANNOUNCEMENT_GOT)
                .withContentPair("id", announcement.getId())
                .withContentPair("time", Long.toString(announcement.getTimeMillis()))
                .withContentPair("title", announcement.getTitle())
                .withContentPair("content", Util.toJson(announcement.getContent()));
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}
