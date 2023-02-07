package net.zhuruoling.omms.central.permission.player;

import java.util.List;

public class Group {
    String name;
    List<String> permittedServer;

    public Group(String name, List<String> permittedServer) {
        this.name = name;
        this.permittedServer = permittedServer;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", permittedServer=" + permittedServer +
                '}';
    }
}
