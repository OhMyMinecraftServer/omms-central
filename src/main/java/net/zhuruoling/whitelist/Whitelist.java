package net.zhuruoling.whitelist;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Whitelist {
    @SerializedName("players")
    String[] players;
    @SerializedName("name")
    String name;
    public Whitelist(
            String[] players,
            String name
    ){
        this.players = players;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "Whitelist{" +
                "players=" + Arrays.toString(players) +
                ", name='" + name + '\'' +
                '}';
    }

    public boolean containsPlayer(String player){
        return Arrays.stream(players).toList().contains(player);
    }
}
