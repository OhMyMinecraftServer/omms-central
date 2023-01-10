package net.zhuruoling.omms.central.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.tree.CommandNode;
import kotlin.Unit;
import net.zhuruoling.omms.central.configuration.Configuration;
import net.zhuruoling.omms.central.console.CommandSourceStack;
import net.zhuruoling.omms.central.console.ConsoleCommandHandler;
import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.network.broadcast.Target;
import net.zhuruoling.omms.central.network.session.request.InitRequest;
import net.zhuruoling.omms.central.whitelist.WhitelistManager;
import org.slf4j.Logger;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Util {


    public static final String PRODUCT_NAME = "Oh My Minecraft Server Central";
    public static final String PRODUCT_NAME_SHORT = "OMMS Central";
    public static final String LOCK_NAME = "omms.lck";

    public static final Target TARGET_CHAT = new Target("224.114.51.4", 10086);
    public static final Target TARGET_CONTROL = new Target("224.114.51.4", 10087);
    public static final String[] DATA_FOLDERS = {
            "controllers",
            "announcements",
            "whitelists",
            "plugins",
    };

    public static final String[] PERMISSION_OPERATIONS = {
            "accept","deny","create","delete"
    };

    public static final long PROTOCOL_VERSION = InitRequest.VERSION_BASE + 0x1;

    public static final Gson gson = new GsonBuilder().serializeNulls().create();


    public static final String[] BUILTIN_COMMANDS = {
            "WHITELIST_CREATE",
            "WHITELIST_LIST",
            "WHITELIST_GET",
            "WHITELIST_ADD",
            "WHITELIST_REMOVE",
            "WHITELIST_DELETE",

            "PERMISSION_CREATE",
            "PERMISSION_DELETE",
            "PERMISSION_GRANT",
            "PERMISSION_DENY",
            "PERMISSION_LIST",

            "CONTROLLERS_LIST",
            "CONTROLLERS_EXECUTE",
            "CONTROLLERS_GET",
            "SYSINFO_GET",

            "END",
    };

    public static boolean fuzzySearch(String a, String b) {
        boolean result = false;

        return false;
    }

    public static String getTimeCode() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"));
    }

    public static int calculateTokenByDate(int password) {
        Date date = new Date();
        int i = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(date));
        int j = Integer.parseInt(new SimpleDateFormat("hhmm").format(date));
        int k = new SimpleDateFormat("yyyyMMddhhmm").format(date).hashCode();
        return calculateToken(password, i, j, k);
    }

    public static boolean resloveTokenByDate(int token, int password) {
        Date date = new Date();
        int i = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(date));
        int j = Integer.parseInt(new SimpleDateFormat("hhmm").format(date));
        int k = new SimpleDateFormat("yyyyMMddhhmm").format(date).hashCode();
        return resloveToken(token, password, i, j, k);
    }


    public static int calculateToken(int password, int i, int j, int k) {
        int token = 114514;
        token += i;
        token += (j - k);
        token = password ^ token;
        return token;
    }

    public static boolean resloveToken(int token, int password, int i, int j, int k) {
        int t = token;
        int var1 = t ^ password;

        var1 = var1 - i - (j - k);
        return var1 == 114514;
    }


    public static boolean fileExists(String fileName) {
        try {
            new FileReader(fileName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static FileLock acquireLock(RandomAccessFile file) {
        try {
            FileChannel channel = file.getChannel();
            return channel.tryLock();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void releaseLock(FileLock lock) {
        try {
            var ch = lock.acquiredBy();
            lock.release();
            ch.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getWorkingDir() {
        File directory = new File("");
        return directory.getAbsolutePath();
    }

    public static String randomStringGen(int len){
        return randomStringGen(len, true, true);
    }

    public static String randomStringGen(int len, boolean hasInteger, boolean hasUpperLetter) {
        String ch = "abcdefghijklmnopqrstuvwxyz" + (hasUpperLetter ? "ABCDEFGHIGKLMNOPQRSTUVWXYZ" : "") + (hasInteger ? "0123456789" : "");
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < len; i++) {
            Random random = new Random(System.nanoTime());
            int num = random.nextInt(62);
            stringBuffer.append(ch.charAt(num));
        }
        return stringBuffer.toString();
    }

    public static String joinFilePaths(String... pathComponent) {
        var paths = pathComponent.clone();
        var path = new StringBuilder();
        path.append(Util.getWorkingDir());
        Arrays.stream(paths).toList().forEach(x -> {
            path.append(File.separator);
            path.append(x);
        });
        return path.toString();
    }

    public static void generateExample() {

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createFolder(String path, Logger logger) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            logger.info("Created folder " + path);
        }

    }

    public static String base64Encode(String content) {
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    public static void createConfig(Logger logger) {
        logger.warn("CONFIG NOT EXIST,creating.");
        try {
            if (!new Util().createFile(Util.getWorkingDir() + File.separator + "config.json")) {
                logger.error("Unable to create file:" + Util.getWorkingDir() + File.separator + "config.json");
                System.exit(-1);
            }
            logger.info("Created Config,writing default config.");
            Gson gson = new GsonBuilder().serializeNulls().create();
            String cont = gson.toJson(new Configuration(50000, "OMMS-Central", 50001));
            File fp = new File(Util.getWorkingDir() + File.separator + "config.json");
            FileOutputStream stream = new FileOutputStream(fp);
            OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
            writer.append(cont);
            writer.close();
            stream.close();
            logger.info("Created Config.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listAll(Logger logger) {
        logger.info("Listing controllers");
        ControllerManager.INSTANCE.getControllers().forEach((s, controllerInstance) -> {
            for (String s1 : UtilKt.controllerPrettyPrinting(controllerInstance.controller()).split("\n")) {
                logger.info(s1);
            }
            logger.info("\t-%s".formatted(controllerInstance.controller().toString()));
        });
        logger.info("Listing Whitelist contents:");
        if (!WhitelistManager.INSTANCE.isNoWhitelist()) {
            WhitelistManager.INSTANCE.forEach(entry -> {
                for (String s : UtilKt.whitelistPrettyPrinting(entry.getValue()).split("\n")) {
                    logger.info(s);
                }
                return Unit.INSTANCE;
            });
        } else {
            logger.warn("No Whitelist added.");
        }
    }

    public static ArrayList<String> genCommandTree() {
        var root = ConsoleCommandHandler.getDispatcher().getRoot();
        ArrayList<String> lines = new ArrayList<>();
        walkCommandTree(root, 0, lines);
        return lines;
    }

    private static void walkCommandTree(CommandNode<CommandSourceStack> node, int depth, ArrayList<String> lines) {
        if (node.getChildren().isEmpty()) {
            lines.add("  ".repeat(depth) + "-" + node.toString());
        } else {
            lines.add("  ".repeat(depth) + "+" + node.toString());
            node.getChildren().forEach(node1 -> {
                walkCommandTree(node1, depth + 1, lines);
            });
        }
    }

    public static <WDNMD> WDNMD fromJson(String content, Class<WDNMD> clazz) {
        return gson.fromJson(content, clazz);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj, obj.getClass());
    }

    public static Target generateRandomTarget() {
        return new Target(
                "224.114.%d.%d".formatted(
                        new Random(System.nanoTime()).nextInt(0, 250),
                        new Random(System.nanoTime()).nextInt(0, 250)
                ), new Random(System.nanoTime()).nextInt(8080, 25565)
        );
    }

    public boolean createFile(String filePath) throws IOException {
        return new File(filePath).createNewFile();
    }


}
