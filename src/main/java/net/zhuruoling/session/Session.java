package net.zhuruoling.session;

import java.net.Socket;

public class Session {
    Socket socket;
    byte[] key;
    public Session(Socket socket, byte[] key){
        this.socket = socket;
        this.key = key;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }
}
