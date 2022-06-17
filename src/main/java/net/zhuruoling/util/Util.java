package net.zhuruoling.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.zhuruoling.configuration.Configuration;
import net.zhuruoling.permcode.PermissionManager;
import net.zhuruoling.scontrol.SControlClient;
import net.zhuruoling.scontrol.SControlClientFileReader;
import net.zhuruoling.whitelist.Whitelist;
import net.zhuruoling.whitelist.WhitelistReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class Util {


    public static final String PRODUCT_NAME = "Oh My Minecraft Server Central";
    public static final String PRODUCT_NAME_SHORT = "OMMS Central";
    public static final String[] dataFolders = {
            "controllers",
            "broadcasts",
            "whitelists",
            "plugins",
    };
    public static boolean fileExists(String fileName){
        try {
            new FileReader(fileName);
            return true;
        }
        catch (Exception e){
            return false;
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
