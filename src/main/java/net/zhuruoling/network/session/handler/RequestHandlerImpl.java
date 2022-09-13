package net.zhuruoling.network.session.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.permission.PermissionChange;
import net.zhuruoling.permission.PermissionManager;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.message.Message;
import net.zhuruoling.network.session.message.MessageBuilderKt;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.network.session.HandlerSession;
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
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("DuplicatedCode")
public class RequestHandlerImpl extends RequestHandler {
    final Logger logger = LoggerFactory.getLogger("InternalCommandHandler");

    public RequestHandlerImpl() {
        super("BUILTIN");
    }
    @Override
    public void handle(Request command, HandlerSession session) {
        var encryptedConnector = session.getEncryptedConnector();
        try {
            Gson gson = new Gson();
            logger.info("Received command:" + command.getRequest() + " with load:" + Arrays.toString(command.getLoad()));
            var load = command.getLoad();

            switch (command.getRequest()) {

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
                    String[] whitelistNames = new String[Objects.requireNonNull(whitelists).size()];
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


                case "PERMISSION_CREATE" -> {
                    if (!session.getPermissions().contains(Permission.PERMISSION_MODIFY))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));
                    try {
                        PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.CREATE, Integer.parseInt(command.getLoad()[0]), null));
                    }
                    catch (Exception e){
                        encryptedConnector.println(MessageBuilderKt.build(Result.OPERATION_ALREADY_EXISTS));
                    }
                }
                case "PERMISSION_DELETE" -> {
                    if (!session.getPermissions().contains(Permission.PERMISSION_MODIFY))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));
                    try {
                        PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.DELETE, Integer.parseInt(command.getLoad()[0]), null));
                    }
                    catch (Exception e){
                        encryptedConnector.println(MessageBuilderKt.build(Result.OPERATION_ALREADY_EXISTS));
                    }
                }
                case "PERMISSION_ADD" -> {
                    if (!session.getPermissions().contains(Permission.PERMISSION_MODIFY))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));
                    try {
                        PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.ADD, Integer.parseInt(command.getLoad()[0]), null));
                    }
                    catch (Exception e){
                        encryptedConnector.println(MessageBuilderKt.build(Result.OPERATION_ALREADY_EXISTS));
                    }
                }
                case "PERMISSION_REMOVE" -> {
                    if (!session.getPermissions().contains(Permission.PERMISSION_MODIFY))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));
                    try {
                        PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.REMOVE, Integer.parseInt(command.getLoad()[0]), null));
                    }
                    catch (Exception e){
                        encryptedConnector.println(MessageBuilderKt.build(Result.OPERATION_ALREADY_EXISTS));
                    }
                }
                case "PERMISSION_LIST" -> {
                    if (!session.getPermissions().contains(Permission.PERMISSION_LIST))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));

                }


                case "CONTROLLER_LIST" -> {
                    if (!session.getPermissions().contains(Permission.CONTROLLER_GET))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));

                }
                case "CONTROLLER_EXECUTE" -> {
                    if (!session.getPermissions().contains(Permission.CONTROLLER_EXECUTE))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));

                }
                case "CONTROLLER_GET" -> {
                    if (!session.getPermissions().contains(Permission.CONTROLLER_GET))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));

                }
                case "SYSINFO_GET" -> {
                    if (!session.getPermissions().contains(Permission.SERVER_OS_CONTROL))encryptedConnector.println(MessageBuilderKt.build(Result.PERMISSION_DENIED));

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
