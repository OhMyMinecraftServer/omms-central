package net.zhuruoling.broadcast;

public class Broadcast {
    public int time;
    public String server;
    public String player;
    public String content;

    public String getContent() {
        return content;
    }

    public String getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Broadcast{" +
                "time='" + time + '\'' +
                ", server='" + server + '\'' +
                ", player='" + player + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
