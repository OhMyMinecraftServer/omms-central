package net.zhuruoling.omms.central.network.session.handler.builtin.announcement;

import net.zhuruoling.omms.central.announcement.AnnouncementManager;
import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.permission.Permission;
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