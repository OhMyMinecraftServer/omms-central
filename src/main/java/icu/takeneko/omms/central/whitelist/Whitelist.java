package icu.takeneko.omms.central.whitelist;

import java.util.List;

public interface Whitelist {
    String getName();

    void init();

    boolean contains(String player);

    List<String> getPlayers();

    void addPlayer(String player) throws PlayerAlreadyExistsException;

    void removePlayer(String player) throws PlayerNotFoundException;

    void saveModifiedBuffer();

    void deleteWhitelist();

}
