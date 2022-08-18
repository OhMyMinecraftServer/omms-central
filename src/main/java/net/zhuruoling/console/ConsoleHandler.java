package net.zhuruoling.console;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.broadcast.Broadcast;
import net.zhuruoling.configuration.Configuration;
import net.zhuruoling.main.RuntimeConstants;
import net.zhuruoling.util.CommandIncompleteException;
import net.zhuruoling.util.CanNotFindThatFuckingCommandException;
import net.zhuruoling.util.Util;
import net.zhuruoling.whitelist.WhitelistManager;
import net.zhuruoling.whitelist.WhitelistReader;
import net.zhuruoling.whitelist.WhitelistResult;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsoleHandler{
    private Logger logger = null;
    public ConsoleHandler(Logger logger) {
        this.logger = logger;
    }

    public void handle(String command){
        var parts = command.split(" ");
        parts[0] = parts[0].toUpperCase();
        if  (parts[0].equals("BROADCAST")){
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                message.append(parts[i]);
                message.append(" ");
            }
            logger.info("Sending message:" + message);
            Broadcast broadcast = new Broadcast();
            broadcast.setChannel("GLOBAL");
            broadcast.setContent(message.toString());
            broadcast.setPlayer(Util.randomStringGen(8));
            broadcast.setServer("OMMS CENTRAL");
            Objects.requireNonNull(RuntimeConstants.INSTANCE.getUdpBroadcastSender()).send(Util.TARGET_CHAT, new Gson().toJson(broadcast, Broadcast.class));
            return;
        }
        if (parts[0].equals("WHITELIST_LIST")){
            var list = new WhitelistReader().getWhitelists();
            ArrayList<String> arrayList = new ArrayList<>();
            list.forEach(x -> {
                arrayList.add(x.getName());
            });
            logger.info("%d whitelists(%s) added to this server.".formatted(arrayList.size(),arrayList));
            return;
        }
        if (parts.length < 2) throw new CommandIncompleteException("Command Incomplete %s<--[HERE]".formatted(command));
        var commands = new ArrayList<>(Arrays.stream(Util.BUILTIN_COMMANDS).toList());
        commands.add("WHITELIST_QUERY");
        if (commands.contains(parts[0])){
            var cmd = parts[0];
            switch (cmd){
                case "WHITELIST_ADD" -> {
                    var result = WhitelistManager.addToWhiteList(parts[1],parts[2]);
                    if (result.equals(WhitelistResult.OK)){
                        logger.info("Successfully added %s to %s".formatted(parts[2],parts[1]));
                        return;
                    }
                    logger.error("Cannot add %s to %s,reason:%s".formatted(parts[2],parts[1],result));
                }
                case "WHITELIST_REMOVE" -> {
                    var result = WhitelistManager.removeFromWhiteList(parts[1],parts[2]);
                    if (result.equals(WhitelistResult.OK)){
                        logger.info("Successfully removed %s from %s".formatted(parts[2],parts[1]));
                        return;
                    }
                    logger.error("Cannot remove %s from %s,reason:%s".formatted(parts[2],parts[1],result));
                }
                case "WHITELIST_GET" -> {
                    var list = new WhitelistReader().getWhitelists();
                    AtomicBoolean succeed = new AtomicBoolean(false);
                    list.forEach(x -> {
                        if (x.getName().equals(parts[1])){
                            logger.info(x.toString());
                            succeed.set(true);
                        }
                    });
                    if (succeed.get()){
                        return;
                    }
                    logger.error("Whitelist %s does not exist.".formatted(parts[1]));
                }
                case "WHITELIST_QUERY" -> {
                    var whitelists = new WhitelistReader().getWhitelists();
                    ArrayList<String> names = new ArrayList<>();
                    whitelists.forEach(x -> {
                        if (x.containsPlayer(parts[1])){
                            names.add(x.getName());
                        }
                    });
                    if (names.isEmpty()){
                        logger.info("Player %s does not exist in any whitelists.".formatted(parts[1]));
                        return;
                    }
                    logger.info("Player %s exists in whitelist:%s.".formatted(parts[1],names));
                }
                default -> {

                }
            }
        }
        else throw new CanNotFindThatFuckingCommandException("Command %s does not exist.".formatted(command));
    }
}
