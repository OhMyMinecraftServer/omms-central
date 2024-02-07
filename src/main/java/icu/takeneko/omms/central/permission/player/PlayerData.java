package icu.takeneko.omms.central.permission.player;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerData {
    String playerName;
    List<String> inGroup;
    List<String> grantedServer;

    public PlayerData(String playerName, List<String> inGroup, List<String> grantedServer) {
        this.playerName = playerName;
        this.inGroup = inGroup;
        this.grantedServer = grantedServer;
    }

    @Override
    public @NotNull String toString() {
        return "PlayerData{" +
                "playerName='" + playerName + '\'' +
                ", inGroup=" + inGroup +
                ", grantedServer=" + grantedServer +
                '}';
    }
}
