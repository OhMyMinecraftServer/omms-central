package net.zhuruoling.omms.central.network.session;

import net.zhuruoling.omms.central.controller.console.ControllerConsole;
import net.zhuruoling.omms.central.network.session.server.SessionServer;
import net.zhuruoling.omms.central.permission.Permission;

import java.util.HashMap;
import java.util.List;

public class SessionContext {
    FuseEncryptedSocket encryptedConnector;
    Session session;
    SessionServer server;
    List<Permission> permissions;
    HashMap<String, ControllerConsole> controllerConsoleMap = new HashMap<>();

    public SessionContext(SessionServer server, FuseEncryptedSocket encryptedConnector, Session session, List<Permission> permissions) {
        this.encryptedConnector = encryptedConnector;
        this.session = session;
        this.permissions = permissions;
        this.server = server;
    }

    public FuseEncryptedSocket getEncryptedConnector() {
        return encryptedConnector;
    }

    public void setEncryptedConnector(FuseEncryptedSocket encryptedConnector) {
        this.encryptedConnector = encryptedConnector;
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

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public HashMap<String, ControllerConsole> getControllerConsoleMap() {
        return controllerConsoleMap;
    }

    public SessionServer getServer() {
        return server;
    }

    public void setServer(SessionServer server) {
        this.server = server;
    }
}
