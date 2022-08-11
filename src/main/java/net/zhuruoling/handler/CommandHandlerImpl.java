package net.zhuruoling.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kotlin.NotImplementedError;
import net.zhuruoling.EncryptedConnector;
import net.zhuruoling.command.Command;
import net.zhuruoling.configuration.ConfigReader;
import net.zhuruoling.message.Message;
import net.zhuruoling.message.MessageBuilderKt;
import net.zhuruoling.permcode.Permission;
import net.zhuruoling.scontrol.SControlClientFileReader;
import net.zhuruoling.session.HandlerSession;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;
import net.zhuruoling.whitelist.Whitelist;
import net.zhuruoling.whitelist.WhitelistManager;
import net.zhuruoling.whitelist.WhitelistReader;
import net.zhuruoling.whitelist.WhitelistResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
                case "WHITELIST_CREATE" -> {
                    if (!session.getPermissions().contains(Permission.WHITELIST_CREATE))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));
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
                    } catch (Exception e) {
                        logger.error("An exception occurred:" + e.getMessage());
                        e.printStackTrace();
                        encryptedConnector.println("{\"msg\":\"INTERNAL_EXCEPTION\",\"load\":[]}");

                    }
                }

                case "WHITELIST_LIST" -> {
                    var whitelists = new WhitelistReader().getWhitelists();
                    if (whitelists == null){
                        encryptedConnector.println(MessageBuilderKt.build(WhitelistResult.NO_WHITELIST));

                    }
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
                        encryptedConnector.println(gson.toJson(new Message(WhitelistResult.WHITELIST_NOT_EXIST.name(), new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(gson.toJson(new Message("OK", new WhitelistReader().read(wlName).getPlayers())));
                }

                case "WHITELIST_ADD" -> {
                    if (!session.getPermissions().contains(Permission.WHITELIST_ADD))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));

                    String whiteName = command.getLoad()[0];
                    String player = command.getLoad()[1];

                    if (new WhitelistReader().read(whiteName) == null) {
                        encryptedConnector.println(gson.toJson(new Message(WhitelistResult.WHITELIST_NOT_EXIST.name(), new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(MessageBuilderKt.build(WhitelistManager.addToWhiteList(whiteName, player)));
                }
                case "WHITELIST_REMOVE" -> {
                    if (!session.getPermissions().contains(Permission.WHITELIST_REMOVE))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));

                    String whiteName = command.getLoad()[0];
                    String player = command.getLoad()[1];
                    if (new WhitelistReader().read(whiteName) == null) {
                        encryptedConnector.println(gson.toJson(new Message(WhitelistResult.WHITELIST_NOT_EXIST.name(), new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(MessageBuilderKt.build(WhitelistManager.removeFromWhiteList(whiteName, player)));
                }
                case "END" -> {
                    encryptedConnector.println(MessageBuilderKt.build(Result.OK));
                    session.getSession().getSocket().close();
                }
                case "PING" -> encryptedConnector.println(MessageBuilderKt.build("PONG",new String[]{}));
                default -> throw new OperationNotSupportedException();
                case "WHITELIST_DELETE" -> {
                    String name = command.getLoad()[0];
                    var file = new File(Util.joinFilePaths("whitelists",name + ".json"));
                    if (file.exists()){
                        boolean result = file.delete();
                        if (result){
                            encryptedConnector.println(MessageBuilderKt.build(Result.OK));
                            return;
                        }
                        encryptedConnector.println(MessageBuilderKt.build(Result.FAIL));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
