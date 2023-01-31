package net.zhuruoling.omms.central.whitelist;

import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import kotlinx.serialization.Serializable;
import org.jetbrains.annotations.NotNull;

@Serializable
public class Whitelist {
    @SerializedName("players")
    String[] players;
    @SerializedName("name")
    String name;

    public Whitelist(String[] players, String name) {
        this.players = players;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String[] getPlayers() {
        return this.players;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    public @NotNull String toString() {
        return "Whitelist{players=" + Arrays.toString(this.players) + ", name='" + this.name + "'}";
    }

    public boolean containsPlayer(String player) {
        return Arrays.stream(this.players).toList().contains(player);
    }
}
