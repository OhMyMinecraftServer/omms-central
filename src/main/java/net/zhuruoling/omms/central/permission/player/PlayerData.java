package net.zhuruoling.omms.central.permission.player;

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
    public String toString() {
        return "PlayerData{" +
                "playerName='" + playerName + '\'' +
                ", inGroup=" + inGroup +
                ", grantedServer=" + grantedServer +
                '}';
    }
}
