package net.zhuruoling.omms.central.network.session.handler.builtin;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;

public class EndRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(Request request, HandlerSession session) {
        return null;//do nothing
    }

    @Override
    public Permission requiresPermission() {
        return null; //does not require any permission
    }
}
