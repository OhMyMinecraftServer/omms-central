package net.zhuruoling.controller;

import java.util.ArrayList;
import java.util.List;

public class Status {
    boolean isAlive = false;

    boolean isQueryAble = false;
    String name;
    ControllerTypes type = ControllerTypes.FABRIC;
    int playerCount = 0;
    int maxPlayerCount = 0;
    List<String> players = new ArrayList<>();


    public Status() {
    }

    public boolean isQueryAble() {
        return isQueryAble;
    }

    public void setQueryAble(boolean queryAble) {
        isQueryAble = queryAble;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ControllerTypes getType() {
        return type;
    }

    public void setType(ControllerTypes type) {
        this.type = type;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public Status(ControllerTypes type, int playerCount, int maxPlayerCount, List<String> players) {
        this.type = type;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.players = players;
    }
}
