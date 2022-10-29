package net.zhuruoling.network.session.handler.builtin.announcement;

import net.zhuruoling.announcement.AnnouncementManager;
import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;

import java.util.Locale;
import java.util.Objects;

public class GetAnnouncementRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
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
    public Permission requiresPermission() {
        return Permission.ANNOUNCEMENT_READ;
    }
}
