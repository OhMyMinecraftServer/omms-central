package icu.takeneko.omms.central.controller;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Status {
    boolean isAlive = false;
    boolean isQueryable = false;
    String name;
    String type = "";
    int playerCount = 0;
    int maxPlayerCount = 0;
    List<String> players = new ArrayList<>();

    public Status() {
    }

    public boolean isQueryable() {
        return isQueryable;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Status(String type, int playerCount, int maxPlayerCount, List<String> players) {
        this.type = type;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.players = players;
    }
}
