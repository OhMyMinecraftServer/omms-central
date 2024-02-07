package icu.takeneko.omms.central.whitelist;

import java.util.List;

public abstract class Whitelist {
    abstract public String getName();
    abstract public void init();
    abstract public boolean contains(String player);

    abstract public List<String> getPlayers();

    abstract public void addPlayer(String player) throws PlayerAlreadyExistsException;

    abstract public void removePlayer(String player) throws PlayerNotFoundException;

    abstract public void saveModifiedBuffer();

    abstract public void deleteWhitelist();

}
