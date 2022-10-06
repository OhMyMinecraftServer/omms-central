package net.zhuruoling.network.session.handler;

import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;

public abstract class RequestHandler {

    public RequestHandler(String register) {
        this.register = register;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    private String register;
    abstract public Response handle(Request request, HandlerSession session);

    abstract public Permission requiresPermission();



}
