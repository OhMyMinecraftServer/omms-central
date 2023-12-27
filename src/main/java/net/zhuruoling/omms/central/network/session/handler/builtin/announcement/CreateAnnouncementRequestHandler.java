package net.zhuruoling.omms.central.network.session.handler.builtin.announcement;

import net.zhuruoling.omms.central.announcement.Announcement;
import net.zhuruoling.omms.central.announcement.AnnouncementManager;
import net.zhuruoling.omms.central.announcement.ContentType;
import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateAnnouncementRequestHandler extends BuiltinRequestHandler {
    @Override
    public @Nullable Response handle(@NotNull Request request, SessionContext session) {
        String id = request.getContent("id") != null ? request.getContent("id") : Util.generateRandomString(16);
        long time = request.getContent("time") != null ? Long.parseLong(request.getContent("time")) : System.currentTimeMillis();
        var typeString = request.getContent("type");
        if (typeString == null)typeString = ContentType.STRING.name();
        ContentType type = ContentType.valueOf(typeString);
        String title = request.getContent("title");
        var json = request.getContent("content");
        if (title == null || json == null){
            return new Response().withResponseCode(Result.INVALID_ARGUMENTS);
        }
        String[] content = Util.fromJson(request.getContent("content"), String[].class);
        var announcement = new Announcement(id, time, title, content, type);
        AnnouncementManager.INSTANCE.create(announcement);
        return new Response().withResponseCode(Result.ANNOUNCEMENT_CREATED).withContentPair("id", id).withContentPair("time", String.valueOf(time));
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.ANNOUNCEMENT_CREATE;
    }
}