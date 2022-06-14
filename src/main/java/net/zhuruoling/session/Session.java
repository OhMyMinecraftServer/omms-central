package net.zhuruoling.session;

import java.net.Socket;

public class Session {
    Socket socket;
    byte[] key;
    public Session(Socket socket, byte[] key){
        this.socket = socket;
        this.key = key;
    }
}
