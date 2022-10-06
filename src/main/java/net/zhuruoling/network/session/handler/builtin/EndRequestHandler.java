package net.zhuruoling.network.session.handler.builtin;

import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;

public class EndRequestHandler extends BuiltinRequestHandler {


    @Override
    public Response handle(Request request, HandlerSession session) {
        return null;
    }

    @Override
    public Permission requiresPermission() {
        return null; //does not require any permission
    }
}
