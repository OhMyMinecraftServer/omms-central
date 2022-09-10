package net.zhuruoling.network.session.handler;

import net.zhuruoling.request.Request;
import net.zhuruoling.network.session.HandlerSession;

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
    abstract public void handle(Request command, HandlerSession session);

}
