package net.zhuruoling.omms.central.network.session;

import net.zhuruoling.omms.central.network.EncryptedSocket;
import net.zhuruoling.omms.central.permission.Permission;

import java.util.List;

public class SessionContext {
    EncryptedSocket encryptedConnector;
    Session session;

    List<Permission> permissions;

    public SessionContext(EncryptedSocket encryptedConnector, Session session, List<Permission> permissions) {
        this.encryptedConnector = encryptedConnector;
        this.session = session;
        this.permissions = permissions;
    }

    public EncryptedSocket getEncryptedConnector() {
        return encryptedConnector;
    }

    public void setEncryptedConnector(EncryptedSocket encryptedConnector) {
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
