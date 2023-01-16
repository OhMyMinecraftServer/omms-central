package net.zhuruoling.omms.central.network.session.handler.builtin.announcement;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.util.Util;

public class CreateAnnouncementRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        String id = request.getContent("id") != null ? request.getContent("id") : Util.randomStringGen(16);
        long time = request.getContent("time") != null ? Long.parseLong(request.getContent("time")) : System.currentTimeMillis();
        String title = request.getContent("title");
        var json = request.getContent("content");
        if (title == null || json == null){
            return new Response().withResponseCode(Result.INVALID_ARGUMENTS);
        }
        String[] content = Util.fromJson(request.getContent("content"), String[].class);

        return null;
    }

    @Override
    public Permission requiresPermission() {
        return Permission.ANNOUNCEMENT_CREATE;
    }
}
