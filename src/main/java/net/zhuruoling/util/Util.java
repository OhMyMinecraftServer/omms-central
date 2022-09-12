package net.zhuruoling.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.configuration.Configuration;
import net.zhuruoling.network.broadcast.Target;
import net.zhuruoling.whitelist.Whitelist;
import net.zhuruoling.whitelist.WhitelistReader;
import org.slf4j.Logger;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class Util {


    public static final String PRODUCT_NAME = "Oh My Minecraft Server Central";
    public static final String PRODUCT_NAME_SHORT = "OMMS Central";
    public static final String LOCK_NAME = "omms.lck";

    public static final Target TARGET_CHAT = new Target("224.114.51.4", 10086);
    public static final Target TARGET_CONTROL = new Target("224.114.51.4", 10087);
    public static final String[] DATA_FOLDERS = {
            "controllers",
            "broadcasts",
            "whitelists",
            "plugins",
    };

    public static final String[] BUILTIN_COMMANDS = {
            "WHITELIST_CREATE",
            "WHITELIST_LIST",
            "WHITELIST_GET",
            "WHITELIST_ADD",
            "WHITELIST_REMOVE",
            "WHITELIST_DELETE",

            "PERMISSION_CREATE",// TODO: 2022/9/12
            "PERMISSION_DELETE",// TODO: 2022/9/12
            "PERMISSION_ADD",// TODO: 2022/9/12
            "PERMISSION_REMOVE",// TODO: 2022/9/12
            "PERMISSION_LIST",// TODO: 2022/9/12

            "CONTROLLER_LIST",// TODO: 2022/9/12
            "CONTROLLER_EXECUTE",// TODO: 2022/9/12
            "CONTROLLER_GET",// TODO: 2022/9/12
            "SYSINFO_GET",// TODO: 2022/9/12

            "END",
    };

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

    public static String randomStringGen(int len) {
        String ch = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
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

        logger.info("Listing Whitelist contents:");
        WhitelistReader reader_ = new WhitelistReader();
        if (reader_.isFail()) {
            logger.error("Failed to read Whitelists.");
            System.exit(1);
        }

        if (!reader_.isNoWhitelist()) {
            List<Whitelist> whitelists = reader_.getWhitelists();
            whitelists.forEach(client -> logger.info("  -" + client.toString()));
        } else {
            logger.warn("No Whitelist added.");
        }
    }

    public boolean createFile(String filePath) throws IOException {
        return new File(filePath).createNewFile();
    }


}
