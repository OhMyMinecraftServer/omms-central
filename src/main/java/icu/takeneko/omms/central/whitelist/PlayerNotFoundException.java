package icu.takeneko.omms.central.whitelist;

public class PlayerNotFoundException extends Exception {
    String player;
    String whitelist;

    public PlayerNotFoundException(String whitelist, String player) {
        super("Cannot find player %s in whitelist %s.".formatted(player, whitelist));
        this.player = player;
        this.whitelist = whitelist;
    }

    public String getWhitelist() {
        return whitelist;
    }

    public String getPlayer() {
        return player;
    }
}
