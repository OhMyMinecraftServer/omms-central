package net.zhuruoling.network.session.handler.builtin.announcement;

import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;

public class DeleteAnnouncementRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {

        return null;
    }

    @Override
    public Permission requiresPermission() {
        return Permission.ANNOUNCEMENT_DELETE;
    }
}
