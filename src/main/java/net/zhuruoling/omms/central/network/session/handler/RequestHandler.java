package net.zhuruoling.omms.central.network.session.handler;

import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.old.session.SessionContext;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import org.jetbrains.annotations.Nullable;

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
    abstract public @Nullable Response handle(Request request, SessionContext session);

    abstract public @Nullable Permission requiresPermission();



}
