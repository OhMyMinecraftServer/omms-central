package net.zhuruoling.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.zhuruoling.configuration.Configuration;
import net.zhuruoling.network.UdpBroadcastSender;
import net.zhuruoling.scontrol.SControlClient;
import net.zhuruoling.scontrol.SControlClientFileReader;
import net.zhuruoling.whitelist.Whitelist;
import net.zhuruoling.whitelist.WhitelistReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Util {


    public static final String PRODUCT_NAME = "Oh My Minecraft Server Central";
    public static final String PRODUCT_NAME_SHORT = "OMMS Central";
    public static final String LOCK_NAME = "omms.lck";

    public static final UdpBroadcastSender.Target TARGET_CHAT = new UdpBroadcastSender.Target("224.114.51.4",10086);
    public static final UdpBroadcastSender.Target TARGET_CONTROL = new UdpBroadcastSender.Target("224.114.51.4",10087);
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
            "END",
            "PERMISSION_CREATE",
            "PERMISSION_MODIFY",
            "PERMISSION_REMOVE",
            "PERMISSION_LIST",
            "RUN_MCDR_COMMAND",
            "RUN_MINECRAFT_COMMAND",
    } ;
    public static boolean fileExists(String fileName){
        try {
            new FileReader(fileName);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public static FileLock acquireLock(RandomAccessFile file){
        try {
            FileChannel channel = file.getChannel();
            return channel.tryLock();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void releaseLock(FileLock lock){
        try {
            var ch = lock.acquiredBy();
            lock.release();
            ch.close();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String getWorkingDir(){
        File directory = new File("");
        return directory.getAbsolutePath();
    }

    public boolean createFile(String filePath) throws IOException {
        return new File(filePath).createNewFile();
    }

    public static String randomStringGen(int len){
        String ch = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0;i < len; i++){
            Random random = new Random(System.nanoTime());
            int num = random.nextInt(62);
            stringBuffer.append(ch.charAt(num));
        }
        return stringBuffer.toString();
    }
    public static String joinFilePaths(String... pathComponent){
        var paths = pathComponent.clone();
        var path = new StringBuilder();
        path.append(Util.getWorkingDir());
        Arrays.stream(paths).toList().forEach(x -> {
            path.append(File.separator);
            path.append(x);
        });
        return path.toString();
    }

    public static void generateExample(){
         try {
             final Logger logger = LoggerFactory.getLogger("Util");
             logger.info("Generating Client Example.");
             Gson gson = new GsonBuilder().serializeNulls().create();
             String cont = gson.toJson(new SControlClient("mcdreforged",Util.randomStringGen(8),50010,"127.0.0.1",Util.randomStringGen(6),"",""));
             File fp = new File(Util.getWorkingDir() + File.separator + "clients" + File.separator + "example.json");
             FileOutputStream stream = new FileOutputStream(fp);
             OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
             writer.append(cont);
             writer.close();
             stream.close();
         }
         catch (Exception e){
             e.printStackTrace();
         }
    }
    public static void createFolder(String path, Logger logger){
        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
            logger.info("Created folder " + path);
        }

    }

    public static String base64Encode(String content){
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }
    public static void createConfig(Logger logger){
        logger.warn("CONFIG NOT EXIST,creating.");
        try {
            if (!new Util().createFile(Util.getWorkingDir() + File.separator + "config.json")){
                logger.error("Unable to create file:" + Util.getWorkingDir() + File.separator + "config.json");
                System.exit(-1);
            }
            logger.info("Created Config,writing default config.");
            Gson gson = new GsonBuilder().serializeNulls().create();
            String cont = gson.toJson(new Configuration(50000,"Uranium"));
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
    public static void listAll(Logger logger){
        logger.info("Listing controllers");
        SControlClientFileReader reader = new SControlClientFileReader();
        if (reader.isFail()){
            logger.error("Failed to read controllers.");
            System.exit(1);
        }
        if (!reader.isNoClient()) {
            List<SControlClient> clientList = reader.getClientList();
            clientList.forEach(client -> logger.info( "  -" + client.toString()));
        }
        else {
            logger.warn("No controllers added.");
        }
        logger.info("Listing Whitelist contents:");
        WhitelistReader reader_ = new WhitelistReader();
        if (reader.isFail()){
            logger.error("Failed to read Whitelists.");
            System.exit(1);
        }

        if (!reader_.isNoWhitelist()) {
            List<Whitelist> whitelists = reader_.getWhitelists();
            whitelists.forEach(client -> logger.info( "  -" + client.toString()));
        }
        else {
            logger.warn("No Whitelist added.");
        }
    }





}
