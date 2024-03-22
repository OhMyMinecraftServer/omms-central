package icu.takeneko.omms.central.network.session;

import icu.takeneko.omms.central.controller.console.ControllerConsole;
import icu.takeneko.omms.central.network.session.server.SessionServer;
import icu.takeneko.omms.central.permission.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class SessionContext {
    EncryptedMessageChannel messageChannel;
    Session session;
    SessionServer server;
    List<Permission> permissions;
    boolean chatMessagePassthroughEnabled = false;
    @NotNull HashMap<String, ControllerConsole> controllerConsoleMap = new HashMap<>();

    public SessionContext(SessionServer sessionServer, EncryptedMessageChannel sessionChannel, Session session, List<Permission> permissions) {
        this.messageChannel = sessionChannel;
        this.session = session;
        this.permissions = permissions;
        this.server = sessionServer;
    }

    public EncryptedMessageChannel getChannel() {
        return messageChannel;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public @NotNull HashMap<String, ControllerConsole> getControllerConsoleMap() {
        return controllerConsoleMap;
    }

    public SessionServer getServer() {
        return server;
    }

    public void setServer(SessionServer server) {
        this.server = server;
    }

    public boolean isChatMessagePassthroughEnabled() {
        return chatMessagePassthroughEnabled;
    }

    public void setChatMessagePassthroughEnabled(boolean chatMessagePassthroughEnabled) {
        this.chatMessagePassthroughEnabled = chatMessagePassthroughEnabled;
    }
}
