package net.zhuruoling.omms.central.network.chatbridge;

import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

public class Broadcast {
    public String channel;
    public String server;
    public String player;
    public String content;
    public String id;
    public Broadcast(){
        this.id = Util.generateRandomString(16);
    }

    public Broadcast(String channel, String server, String player, String content) {
        this.channel = channel;
        this.server = server;
        this.player = player;
        this.content = content;
        this.id = Util.generateRandomString(16);
    }

    @Override
    public @NotNull String toString() {
        return "Broadcast{" +
                "channel='" + channel + '\'' +
                ", server='" + server + '\'' +
                ", player='" + player + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}