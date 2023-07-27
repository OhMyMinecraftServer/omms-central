package net.zhuruoling.omms.central.permission.player;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Group {
    String name;
    List<String> permittedServer;

    public Group(String name, List<String> permittedServer) {
        this.name = name;
        this.permittedServer = permittedServer;
    }

    @Override
    public @NotNull String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", permittedServer=" + permittedServer +
                '}';
    }
}
