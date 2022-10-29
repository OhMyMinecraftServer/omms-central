package net.zhuruoling.network.session.handler.builtin.announcement;

import net.zhuruoling.announcement.AnnouncementManager;
import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;

public class ListAnnouncementRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        return new Response()
                .withResponseCode(Result.OK)
                .withContentPair("announcements",
                        Util.toJson(
                                AnnouncementManager.INSTANCE.getAnnouncementMap().keySet()
                        )
                );
    }

    @Override
    public Permission requiresPermission() {
        return Permission.ANNOUNCEMENT_READ;
    }
}
