package icu.takeneko.omms.central.network.session;

import icu.takeneko.omms.central.controller.console.ControllerConsole;
import icu.takeneko.omms.central.network.session.server.SessionServer;
import icu.takeneko.omms.central.permission.Permission;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class SessionContext {
    private final EncryptedMessageChannel messageChannel;
    @Setter
    private Session session;
    @Setter
    private SessionServer server;
    private final List<Permission> permissions;
    @Setter
    private boolean chatMessagePassthroughEnabled = false;
    private final @NotNull HashMap<String, ControllerConsole> controllerConsoleMap = new HashMap<>();
    private final Map<String, String> controllerConsoleRequestIds = new HashMap<>();

    public SessionContext(SessionServer sessionServer, EncryptedMessageChannel sessionChannel, Session session, List<Permission> permissions) {
        this.messageChannel = sessionChannel;
        this.session = session;
        this.permissions = permissions;
        this.server = sessionServer;
    }
}
