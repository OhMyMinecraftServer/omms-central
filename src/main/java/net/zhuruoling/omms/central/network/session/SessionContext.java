package net.zhuruoling.omms.central.network.session;

import net.zhuruoling.omms.central.permission.Permission;

import java.util.List;

public class SessionContext {
    RateLimitEncryptedSocket encryptedConnector;
    Session session;

    List<Permission> permissions;

    public SessionContext(RateLimitEncryptedSocket encryptedConnector, Session session, List<Permission> permissions) {
        this.encryptedConnector = encryptedConnector;
        this.session = session;
        this.permissions = permissions;
    }

    public RateLimitEncryptedSocket getEncryptedConnector() {
        return encryptedConnector;
    }

    public void setEncryptedConnector(RateLimitEncryptedSocket encryptedConnector) {
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
