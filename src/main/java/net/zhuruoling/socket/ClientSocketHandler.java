package net.zhuruoling.socket;


import net.zhuruoling.EncryptedConnector;
import net.zhuruoling.command.Command;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;

@Deprecated
public class ClientSocketHandler extends SocketHandler {

    public ClientSocketHandler(Socket socket) throws IOException {
        super(socket);
    }


    @Override
    public void handle(Command command, Logger logger, EncryptedConnector encryptedConnector) {

        /*
        try {

            Gson gson = new Gson();
            logger.info("Received command:" + command.getCmd() + " with load:" + Arrays.toString(command.getLoad()));
            var load = command.getLoad();
            var lastElementIndex = load.length - 1;
            while(load[lastElementIndex] == null){
                lastElementIndex = lastElementIndex - 1;
            }
            if (!Objects.equals(load[lastElementIndex], Objects.requireNonNull(ConfigReader.read()).getKey())) {
                encryptedConnector.println(gson.toJson(new Message("BAD_KEY", new String[]{}), Message.class));
                return;
            }
            switch (command.getCmd()) {
                case "LIST_SERVER":
                    if (new SControlClientFileReader().isNoClient()) {
                        encryptedConnector.println(gson.toJson(new Message("NO_CLIENTS", new String[]{}), Message.class));
                        break;
                    }
                    var scontrolList = new SControlClientFileReader().getClientList();
                    List<String> ls = new ArrayList<>();
                    scontrolList.forEach(scontrol -> ls.add(scontrol.getName()));
                    encryptedConnector.println(MessageBuilderKt.build(Result.OK, ls.toArray(new String[ls.size()])));
                    break;
                case "PING":
                    encryptedConnector.println(gson.toJson(new Message("OK", new String[]{Objects.requireNonNull(ConfigReader.read()).getServerName()}), Message.class));
                    break;
                case "GET_SERVER_STATUS":
                    var sc = new SControlClientFileReader().read(command.getLoad()[0]);
                    var pingCmd = new Command("PING", new String[]{});
                    try {
                        var sock = new Socket(sc.getIp(), sc.getPort());
                        var conn = new EncryptedConnector(new BufferedReader(new InputStreamReader(sock.getInputStream()))
                                , new PrintWriter(new OutputStreamWriter(sock.getOutputStream())),
                                sc.getKey());
                        conn.send(new GsonBuilder().serializeNulls().create().toJson(pingCmd, Command.class));
                        var result = new GsonBuilder().serializeNulls().create().fromJson(conn.readLine(), Message.class);
                        if (Objects.equals(result.getMsg(), "OK")) {
                            encryptedConnector.println(gson.toJson(new Message("OK", new String[]{}), Message.class));
                            break;
                        }
                    } catch (Exception e) {
                        encryptedConnector.println(gson.toJson(new Message("FAIL", new String[]{e.getMessage()}), Message.class));
                        break;
                    }
                    break;
                case "GET_SERVERS_STATUS":
                    var lst = new SControlClientFileReader().getClientList();
                    var pingCmd1 = new Command("PING", new String[]{});
                    var resultList = new ArrayList<String>();
                    lst.forEach(sc1 -> {
                        try {
                            var sock1 = new Socket(sc1.getIp(), sc1.getPort());
                            var conn = new EncryptedConnector(new BufferedReader(new InputStreamReader(sock1.getInputStream()))
                                    , new PrintWriter(new OutputStreamWriter(sock1.getOutputStream())),
                                    sc1.getKey());
                            conn.send(new GsonBuilder().serializeNulls().create().toJson(pingCmd1, Command.class));
                            var result = new GsonBuilder().serializeNulls().create().fromJson(conn.readLine(), Message.class);
                            if (Objects.equals(result.getMsg(), "OK")) {
                                resultList.add("OK");
                            }
                        } catch (Exception e) {
                            resultList.add(e.getMessage());
                        }
                    });
                    var ld = resultList.toArray(new String[resultList.size()]);

                    encryptedConnector.send(MessageBuilderKt.build(Result.OK, ld));
                    break;
                case "WHITELIST_QUERY":
                    var whitelistName = command.getLoad()[0];
                    var playerName = command.getLoad()[1];
                    if (playerName.contains("bot")) { //bot直接通过
                        encryptedConnector.println(gson.toJson(new Message("OK", new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(MessageBuilderKt.build(WhitelistManager.queryWhitelist(whitelistName, playerName)));
                    break;
                case "WHITELIST_CREATE":
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
                    break;
                case "LAUNCH":
                    var sc2 = new SControlClientFileReader().read(command.getLoad()[0]);
                    var command1 = new Command("PING", new String[]{});
                    boolean available = false;
                    try {
                        var sock = new Socket(sc2.getIp(), sc2.getPort());
                        var conn = new EncryptedConnector(new BufferedReader(new InputStreamReader(sock.getInputStream()))
                                , new PrintWriter(new OutputStreamWriter(sock.getOutputStream())),
                                sc2.getKey());
                        conn.send(new GsonBuilder().serializeNulls().create().toJson(command1, Command.class));
                        var result = new GsonBuilder().serializeNulls().create().fromJson(conn.readLine(), Message.class);
                        if (Objects.equals(result.getMsg(), "OK")) {
                            available = true;
                        }
                    } catch (Exception ignored) {
                    }
                    if (available) {
                        var os = ManagementFactory.getOperatingSystemMXBean();
                        if (os.getName().contains("Windows")) {
                            Runtime run = Runtime.getRuntime();
                            Process process = run.exec("cmd /c start \"%s\"".formatted(sc2.getPath()), null, new File(sc2.getWorkingDir()));
                            encryptedConnector.println(gson.toJson(new Message("OK", new String[]{Long.toString(process.pid())}), Message.class));
                            break;
                        }
                        Runtime run = Runtime.getRuntime();
                        Process process = run.exec(sc2.getPath(), null, new File(sc2.getWorkingDir()));
                        encryptedConnector.println(gson.toJson(new Message("OK", new String[]{Long.toString(process.pid())}), Message.class));
                    } else {
                        encryptedConnector.println(gson.toJson(new Message("SERVER_ALREADY_RUNNING", new String[]{}), Message.class));
                    }
                    break;

                case "WHITELIST_LIST":
                    var whitelists = new WhitelistReader().getWhitelists();
                    String[] whitelistNames = new String[whitelists.size()];
                    for (int i = 0; i < whitelistNames.length; i++) {
                        whitelistNames[i] = whitelists.get(i).getName();
                    }
                    encryptedConnector.println(MessageBuilderKt.build(Result.OK, whitelistNames));
                    break;
                case "WHITELIST_GET":
                    var wlName = command.getLoad()[0];
                    Whitelist wl = new WhitelistReader().read(wlName);
                    if (wl == null) {
                        encryptedConnector.println(gson.toJson(new Message("NO_SUCH_WHITELIST", new String[]{}), Message.class));
                        break;
                    }
                    encryptedConnector.println(gson.toJson(new Message("OK", new WhitelistReader().read(wlName).getPlayers())));
                    break;
                case "WHITELIST_EDIT":
                    String whiteName = command.getLoad()[0];
                    String operation = command.getLoad()[1];
                    String player = command.getLoad()[2];
                    if (new WhitelistReader().read(whiteName) == null) {
                        encryptedConnector.println(gson.toJson(new Message("NO_SUCH_WHITELIST", new String[]{}), Message.class));
                        break;
                    }
                    switch (operation) {
                        case "ADD" -> encryptedConnector.println(MessageBuilderKt.build(WhitelistManager.addToWhiteList(whiteName, player)));
                        case "REMOVE" -> encryptedConnector.println(MessageBuilderKt.build(WhitelistManager.removeFromWhiteList(whiteName, player)));
                        default -> encryptedConnector.println(gson.toJson(new Message("BAD_OPERATION", new String[]{}), Message.class));
                    }
                default:
                    break;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        */

    }

}
