package icu.takeneko.omms.central.network.session.handler.builtin.announcement;

import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.announcement.AnnouncementManager;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeleteAnnouncementRequestHandler extends BuiltinRequestHandler {
    @Override
    public @Nullable Response handle(Request request, SessionContext session) {
        var id = request.getContent("id");
        var announcement = AnnouncementManager.INSTANCE.get(id);
        if (announcement == null) {
            return new Response().withResponseCode(Result.ANNOUNCEMENT_NOT_EXIST).withContentPair("id", id);
        }
        return new Response().withResponseCode(Result.ANNOUNCEMENT_DELETED).withContentPair("id", id);
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.ANNOUNCEMENT_DELETE;
    }
}