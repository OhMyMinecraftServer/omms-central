package icu.takeneko.omms.central.network.session;

import icu.takeneko.omms.central.controller.console.ControllerConsole;
import icu.takeneko.omms.central.network.session.server.SessionServer;
import icu.takeneko.omms.central.permission.Permission;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class SessionContext {
    private final EncryptedMessageChannel messageChannel;
    @Getter
    @Setter
    private Session session;
    @Setter
    @Getter
    private SessionServer server;
    @Getter
    private final List<Permission> permissions;
    @Setter
    @Getter
    private boolean chatMessagePassthroughEnabled = false;
    private final @NotNull HashMap<String, ControllerConsole> controllerConsoleMap = new HashMap<>();

    public SessionContext(SessionServer sessionServer, EncryptedMessageChannel sessionChannel, Session session, List<Permission> permissions) {
        this.messageChannel = sessionChannel;
        this.session = session;
        this.permissions = permissions;
        this.server = sessionServer;
    }

    public EncryptedMessageChannel getChannel() {
        return messageChannel;
    }

    public @NotNull HashMap<String, ControllerConsole> getControllerConsoleMap() {
        return controllerConsoleMap;
    }

}
