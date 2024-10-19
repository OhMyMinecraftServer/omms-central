package icu.takeneko.omms.central.network.session.handler;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.permission.Permission;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
public abstract class RequestHandler {

    public RequestHandler(String register) {
        this.register = register;
    }

    private String register;

    abstract public @Nullable Response handle(Request request, SessionContext session);

    abstract public @Nullable Permission requiresPermission();


}
