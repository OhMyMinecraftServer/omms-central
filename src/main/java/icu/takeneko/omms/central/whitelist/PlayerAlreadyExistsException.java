package icu.takeneko.omms.central.whitelist;

public class PlayerAlreadyExistsException extends Exception {
    String player;
    String whitelist;

    public PlayerAlreadyExistsException(String whitelist, String player) {
        super("Player %s already exists in %s.".formatted(player, whitelist));
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
