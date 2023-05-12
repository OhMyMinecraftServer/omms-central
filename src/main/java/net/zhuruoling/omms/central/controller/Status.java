package net.zhuruoling.omms.central.controller;

import java.util.ArrayList;
import java.util.List;

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

    public void setQueryable(boolean queryable) {
        isQueryable = queryable;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public Status(String type, int playerCount, int maxPlayerCount, List<String> players) {
        this.type = type;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.players = players;
    }

    @Override
    public String toString() {
        return "Status{" +
                "isAlive=" + isAlive +
                ", isQueryable=" + isQueryable +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", playerCount=" + playerCount +
                ", maxPlayerCount=" + maxPlayerCount +
                ", players=" + players +
                '}';
    }
}
