package net.zhuruoling.network.session.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.controller.ControllerManager;
import net.zhuruoling.permission.PermissionChange;
import net.zhuruoling.permission.PermissionManager;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.message.Message;
import net.zhuruoling.network.session.message.MessageBuilderKt;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.system.SystemInfo;
import net.zhuruoling.system.SystemUtil;
import net.zhuruoling.util.Util;
import net.zhuruoling.util.Result;
import net.zhuruoling.whitelist.Whitelist;
import net.zhuruoling.whitelist.WhitelistManager;
import net.zhuruoling.whitelist.WhitelistReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@SuppressWarnings("DuplicatedCode")
public class RequestHandlerImpl extends RequestHandler {
    final Logger logger = LoggerFactory.getLogger("InternalRequestHandler");

    public RequestHandlerImpl() {
        super("BUILTIN");
    }
    @Override
    public void handle(Request request, HandlerSession session) {
        var encryptedConnector = session.getEncryptedConnector();
        try {
            Gson gson = new Gson();
            logger.info("Received request:" + request.getRequest() + " with load:" + Arrays.toString(request.getLoad()));
            var load = request.getLoad();

            switch (request.getRequest()) {

                case "WHITELIST_CREATE" -> {
                    if (!session.getPermissions().contains(Permission.WHITELIST_CREATE))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));
                    var name = request.getLoad()[0];
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
                        encryptedConnector.println(MessageBuilderKt.build(Result.NO_WHITELIST));

                    }
                    String[] whitelistNames = new String[Objects.requireNonNull(whitelists).size()];
                    for (int i = 0; i < whitelistNames.length; i++) {
                        whitelistNames[i] = whitelists.get(i).getName();
                    }
                    encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK, whitelistNames));
                }
                case "WHITELIST_GET" -> {
                    var wlName = request.getLoad()[0];
                    Whitelist wl = new WhitelistReader().read(wlName);
                    if (wl == null) {
                        encryptedConnector.println(gson.toJson(new Message(Result.WHITELIST_NOT_EXIST.name(), new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(gson.toJson(new Message("OK", new WhitelistReader().read(wlName).getPlayers())));
                }
                case "WHITELIST_ADD" -> {
                    if (!session.getPermissions().contains(Permission.WHITELIST_ADD))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));

                    String whiteName = request.getLoad()[0];
                    String player = request.getLoad()[1];

                    if (new WhitelistReader().read(whiteName) == null) {
                        encryptedConnector.println(gson.toJson(new Message(Result.WHITELIST_NOT_EXIST.name(), new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(MessageBuilderKt.build(WhitelistManager.addToWhiteList(whiteName, player)));
                }
                case "WHITELIST_REMOVE" -> {
                    if (!session.getPermissions().contains(Permission.WHITELIST_REMOVE))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));

                    String whiteName = request.getLoad()[0];
                    String player = request.getLoad()[1];
                    if (new WhitelistReader().read(whiteName) == null) {
                        encryptedConnector.println(gson.toJson(new Message(Result.WHITELIST_NOT_EXIST.name(), new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(MessageBuilderKt.build(WhitelistManager.removeFromWhiteList(whiteName, player)));
                }
                case "WHITELIST_DELETE" -> {
                    String name = request.getLoad()[0];
                    var file = new File(Util.joinFilePaths("whitelists",name + ".json"));
                    if (file.exists()){
                        boolean result = file.delete();
                        if (result){
                            encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK));
                            return;
                        }
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.FAIL));
                    }
                }


                case "PERMISSION_CREATE" -> {
                    if (!session.getPermissions().contains(Permission.PERMISSION_MODIFY))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));
                    try {
                        PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.CREATE, Integer.parseInt(request.getLoad()[0]), null));
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK));
                    }
                    catch (Exception e){
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OPERATION_ALREADY_EXISTS));
                    }
                }
                case "PERMISSION_DELETE" -> {
                    if (!session.getPermissions().contains(Permission.PERMISSION_MODIFY))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));
                    try {
                        PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.DELETE, Integer.parseInt(request.getLoad()[0]), null));
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK));
                    }
                    catch (Exception e){
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OPERATION_ALREADY_EXISTS));
                    }
                }
                case "PERMISSION_ADD" -> {
                    if (!session.getPermissions().contains(Permission.PERMISSION_MODIFY))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));
                    try {
                        PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.ADD, Integer.parseInt(request.getLoad()[0]), null));
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK));
                    }
                    catch (Exception e){
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OPERATION_ALREADY_EXISTS));
                    }
                }
                case "PERMISSION_REMOVE" -> {
                    if (!session.getPermissions().contains(Permission.PERMISSION_MODIFY))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));
                    try {
                        PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.REMOVE, Integer.parseInt(request.getLoad()[0]), null));
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK));
                    }
                    catch (Exception e){
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OPERATION_ALREADY_EXISTS));
                    }
                }
                case "PERMISSION_LIST" -> {
                    if (!session.getPermissions().contains(Permission.PERMISSION_LIST))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));
                    var codes = PermissionManager.INSTANCE.getPermissionTable().keySet();
                    var codeStrings = new  ArrayList<String>();
                    codes.forEach(integer -> codeStrings.add(Integer.toString(integer)));
                    encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK, codeStrings));
                }


                case "CONTROLLER_LIST" -> {
                    if (!session.getPermissions().contains(Permission.CONTROLLER_GET))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));
                    var controllerNames = ControllerManager.INSTANCE.getControllers().keySet();
                    encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK, controllerNames));
                }
                case "CONTROLLER_EXECUTE" -> {
                    if (!session.getPermissions().contains(Permission.CONTROLLER_EXECUTE))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));
                    var name = request.getLoad()[0];
                    var controller = ControllerManager.INSTANCE.getControllerByName(name);
                    if (controller == null){
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.CONTROLLER_NOT_EXIST));
                    }
                    else{
                        var command = request.getLoad()[1];
                        ControllerManager.INSTANCE.sendInstruction(controller, command);
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK));
                    }
                }
                case "CONTROLLER_GET" -> {
                    if (!session.getPermissions().contains(Permission.CONTROLLER_GET))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));
                    var name = request.getLoad()[0];
                    var controller = ControllerManager.INSTANCE.getControllerByName(name);
                    if (controller == null){
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.CONTROLLER_NOT_EXIST));
                    }
                    else{
                        encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK, new String[]{gson.toJson(controller)}));
                    }
                }
                case "SYSINFO_GET" -> {
                    if (!session.getPermissions().contains(Permission.SERVER_OS_CONTROL))encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.PERMISSION_DENIED));
                    SystemInfo info = new SystemInfo(SystemUtil.getFileSystemInfo(), SystemUtil.getMemoryInfo(), SystemUtil.getNetworkInfo(), SystemUtil.getProcessorInfo(), SystemUtil.getStorageInfo());
                    encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK, Collections.singleton(gson.toJson(info))));
                }


                case "END" -> {
                    encryptedConnector.println(MessageBuilderKt.build(net.zhuruoling.util.Result.OK));
                    session.getSession().getSocket().close();
                }


                default -> throw new OperationNotSupportedException();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
