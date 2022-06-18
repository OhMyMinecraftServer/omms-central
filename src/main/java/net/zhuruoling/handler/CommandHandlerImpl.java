package net.zhuruoling.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kotlin.NotImplementedError;
import net.zhuruoling.EncryptedConnector;
import net.zhuruoling.command.Command;
import net.zhuruoling.configuration.ConfigReader;
import net.zhuruoling.message.Message;
import net.zhuruoling.message.MessageBuilderKt;
import net.zhuruoling.scontrol.SControlClientFileReader;
import net.zhuruoling.session.HandlerSession;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;
import net.zhuruoling.whitelist.Whitelist;
import net.zhuruoling.whitelist.WhitelistManager;
import net.zhuruoling.whitelist.WhitelistReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CommandHandlerImpl extends CommandHandler {
    Logger logger = LoggerFactory.getLogger("InternalCommandHandler");

    public CommandHandlerImpl() {
        super("BUILTIN");
    }

    @Override
    public void handle(Command command, HandlerSession session) {
        var encryptedConnector = session.getEncryptedConnector();
        try {
            Gson gson = new Gson();
            logger.info("Received command:" + command.getCmd() + " with load:" + Arrays.toString(command.getLoad()));
            var load = command.getLoad();

            switch (command.getCmd()) {
                case "WHITELIST_QUERY" -> {
                    var whitelistName = command.getLoad()[0];
                    var playerName = command.getLoad()[1];
                    if (playerName.contains("bot")) { //bot直接通过
                        encryptedConnector.println(gson.toJson(new Message("OK", new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(MessageBuilderKt.build(WhitelistManager.queryWhitelist(whitelistName, playerName)));
                }
                case "WHITELIST_CREATE" -> {
                    var name = command.getLoad()[0];
                    if (!(new WhitelistReader().isFail()) && (new WhitelistReader().read(name) != null)) {
                        encryptedConnector.println(gson.toJson(new Message("WHITELIST_EXISTS", new String[]{}), Message.class));
                        break;
                    }
                    try {
                        logger.info("Generating Whitelist " + name);
                        Gson gson1 = new GsonBuilder().serializeNulls().create();
                        String[] players = {};
                        String cont = gson1.toJson(new Whitelist(players, name));
                        File fp = new File(Util.getWorkingDir() + File.separator + "whitelists" + File.separator + name + ".json");
                        FileOutputStream stream = new FileOutputStream(fp);
                        OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
                        writer.append(cont);
                        writer.close();
                        stream.close();
                        encryptedConnector.println(gson.toJson(new Message("OK", new String[]{}), Message.class));
                        break;
                    } catch (Exception e) {
                        logger.error("An exception occurred:" + e.getMessage());
                        e.printStackTrace();
                        encryptedConnector.println("{\"msg\":\"INTERNAL_EXCEPTION\",\"load\":[]}");

                    }
                }

                case "WHITELIST_LIST" -> {
                    var whitelists = new WhitelistReader().getWhitelists();
                    String[] whitelistNames = new String[whitelists.size()];
                    for (int i = 0; i < whitelistNames.length; i++) {
                        whitelistNames[i] = whitelists.get(i).getName();
                    }
                    encryptedConnector.println(MessageBuilderKt.build(Result.OK, whitelistNames));
                }

                case "WHITELIST_GET" -> {
                    var wlName = command.getLoad()[0];
                    Whitelist wl = new WhitelistReader().read(wlName);
                    if (wl == null) {
                        encryptedConnector.println(gson.toJson(new Message("NO_SUCH_WHITELIST", new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(gson.toJson(new Message("OK", new WhitelistReader().read(wlName).getPlayers())));
                }

                case "WHITELIST_ADD" -> {
                    String whiteName = command.getLoad()[0];
                    String operation = command.getLoad()[1];
                    String player = command.getLoad()[2];
                    if (new WhitelistReader().read(whiteName) == null) {
                        encryptedConnector.println(gson.toJson(new Message("NO_SUCH_WHITELIST", new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(MessageBuilderKt.build(WhitelistManager.addToWhiteList(whiteName, player)));
                }
                case "WHITELIST_REMOVE" -> {
                    String whiteName = command.getLoad()[0];
                    String operation = command.getLoad()[1];
                    String player = command.getLoad()[2];
                    if (new WhitelistReader().read(whiteName) == null) {
                        encryptedConnector.println(gson.toJson(new Message("NO_SUCH_WHITELIST", new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(MessageBuilderKt.build(WhitelistManager.removeFromWhiteList(whiteName, player)));
                }
                case "END" -> {
                    encryptedConnector.println(MessageBuilderKt.build(Result.OK));
                    session.getSession().getSocket().close();
                }
                default -> throw new OperationNotSupportedException();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
