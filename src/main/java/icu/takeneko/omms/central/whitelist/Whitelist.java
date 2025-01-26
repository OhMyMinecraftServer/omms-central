package icu.takeneko.omms.central.whitelist;

import icu.takeneko.omms.central.foundation.IdHolder;

import java.util.List;

public interface Whitelist extends IdHolder {
    String getName();

    void init();

    boolean contains(String player);

    List<String> getPlayers();

    void addPlayer(String player) throws PlayerAlreadyExistsException;

    void removePlayer(String player) throws PlayerNotFoundException;

    void saveModifiedBuffer();

    void deleteWhitelist();

    @Override
    default String getId() {
        return getName();
    }
}
