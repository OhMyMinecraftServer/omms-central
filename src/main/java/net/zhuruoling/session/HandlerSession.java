package net.zhuruoling.session;

import net.zhuruoling.EncryptedConnector;
import net.zhuruoling.permcode.Permission;

import java.util.List;

public class HandlerSession {
    EncryptedConnector encryptedConnector;
    Session session;

    List<Permission> permissions;

    public HandlerSession(EncryptedConnector encryptedConnector, Session session, List<Permission> permissions) {
        this.encryptedConnector = encryptedConnector;
        this.session = session;
        this.permissions = permissions;
    }

    public EncryptedConnector getEncryptedConnector() {
        return encryptedConnector;
    }

    public void setEncryptedConnector(EncryptedConnector encryptedConnector) {
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
}
