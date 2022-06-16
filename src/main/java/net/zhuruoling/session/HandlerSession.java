package net.zhuruoling.session;

import net.zhuruoling.EncryptedConnector;

public class HandlerSession {
    EncryptedConnector encryptedConnector;
    Session session;

    public HandlerSession(EncryptedConnector encryptedConnector, Session session) {
        this.encryptedConnector = encryptedConnector;
        this.session = session;
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
}
