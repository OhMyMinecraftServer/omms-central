package net.zhuruoling.omms.central.network.session.handler.builtin.announcement;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;

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
