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
