package net.zhuruoling.omms.central.network.session.handler.builtin;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import org.jetbrains.annotations.Nullable;

public class EndRequestHandler extends BuiltinRequestHandler {

    @Override
    public @Nullable Response handle(Request request, HandlerSession session) {
        return null;//do nothing
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return null; //does not require any permission
    }
}