package net.zhuruoling.omms.central.network.session.handler.builtin.announcement;

import net.zhuruoling.omms.central.announcement.AnnouncementManager;
import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.util.Util;

public class ListAnnouncementRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, SessionContext session) {
        return new Response()
                .withResponseCode(Result.ANNOUNCEMENT_LISTED)
                .withContentPair("announcements",
                        Util.toJson(
                                AnnouncementManager.INSTANCE.getAnnouncementMap().keySet()
                        )
                );
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}
