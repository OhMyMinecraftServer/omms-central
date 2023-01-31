package net.zhuruoling.omms.central.network.session.handler.builtin.announcement;

import net.zhuruoling.omms.central.announcement.AnnouncementManager;
import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.util.Util;
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
                .withResponseCode(Result.OK)
                .withContentPair("id", announcement.getId())
                .withContentPair("time", Long.toString(announcement.getTimeMillis()))
                .withContentPair("title", announcement.getTitle())
                .withContentPair("content", Util.toJson(announcement.getContent()));
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.ANNOUNCEMENT_READ;
    }
}
