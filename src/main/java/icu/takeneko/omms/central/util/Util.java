package icu.takeneko.omms.central.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import icu.takeneko.omms.central.command.CommandSourceStack;
import icu.takeneko.omms.central.controller.ControllerHttpClient;
import icu.takeneko.omms.central.controller.ControllerManager;
import icu.takeneko.omms.central.network.chatbridge.Target;
import icu.takeneko.omms.central.whitelist.WhitelistManager;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Util {


    public static final String PRODUCT_NAME = "Oh My Minecraft Server Central Server";
    public static final String PRODUCT_NAME_SHORT = "OMMS Central Server";
    public static final Target TARGET_CHAT = new Target("224.114.51.4", 10086);
    public static final String[] DATA_FOLDERS = {
            "controllers",
            "announcements",
            "whitelists",
            "plugins",
            "scripts"
    };


    public static final Gson gson = new GsonBuilder()
            .addDeserializationExclusionStrategy(new GlobalExclusionStrategy())
            .addSerializationExclusionStrategy(new GlobalExclusionStrategy())
            .serializeNulls()
            .create();

    public static @NotNull String getTimeCode() {
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
        return resolveToken(token, password, i, j, k);
    }
    public static int calculateToken(int password, int i, int j, int k) {
        int token = 114514;
        token += i;
        token += (j - k);
        token = password ^ token;
        return token;
    }

    public static boolean resolveToken(int token, int password, int i, int j, int k) {
        int t = token;
        int var1 = t ^ password;

        var1 = var1 - i - (j - k);
        return var1 == 114514;
    }

    public static @NotNull String getWorkingDirString() {
        File directory = new File("");
        return directory.getAbsolutePath();
    }

    public static @NotNull String generateRandomString(int len) {
        return generateRandomString(len, true, true);
    }

    public static @NotNull String generateRandomString(int len, boolean hasInteger, boolean hasUpperLetter) {
        String ch = "abcdefghijklmnopqrstuvwxyz" + (hasUpperLetter ? "ABCDEFGHIGKLMNOPQRSTUVWXYZ" : "") + (hasInteger ? "0123456789" : "");
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < len; i++) {
            Random random = new Random(System.nanoTime());
            int num = random.nextInt(ch.length() - 1);
            stringBuffer.append(ch.charAt(num));
        }
        return stringBuffer.toString();
    }

    public static @NotNull File fileOf(String @NotNull ... pathComponent) {
        return absolutePath(pathComponent).toFile();
    }

    public static Path absolutePath(String @NotNull ... pathComponent){
        Path path = Path.of(".");
        for (String s : pathComponent) {
            path = path.resolve(s);
        }
        return path;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createFolder(@NotNull String path, @NotNull Logger logger) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            logger.info("Created folder " + path);
        }
    }

    public static String base64Encode(@NotNull String content) {
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    public static void listAll(@NotNull Logger logger) {
        logger.debug("Listing controllers");
        ControllerManager.INSTANCE.getControllers().forEach((s, controllerInstance) -> {
            for (String s1 : UtilKt.controllerPrettyPrinting(controllerInstance).split("\n")) {
                logger.debug(s1);
            }
        });
        logger.info("%d controllers added to this server.".formatted(ControllerManager.INSTANCE.getControllers().size()));
        logger.debug("Listing Whitelist contents:");
        if (!WhitelistManager.INSTANCE.isNoWhitelist()) {
            WhitelistManager.INSTANCE.forEach(entry -> {
                for (String s : UtilKt.whitelistPrettyPrinting(entry.getValue()).split("\n")) {
                    logger.debug(s);
                }
                return Unit.INSTANCE;
            });
            logger.info("%d whitelists added to this server.".formatted(WhitelistManager.INSTANCE.getAllWhitelist().size()));
        } else {
            logger.warn("No Whitelist added.");
        }
    }

    public static void listAllByCommandSource(@NotNull CommandSourceStack logger) {
        ControllerManager.INSTANCE.getControllers().forEach((s, controllerInstance) -> {
            for (String s1 : UtilKt.controllerPrettyPrinting(controllerInstance).split("\n")) {
                logger.sendFeedback(s1);
            }
        });
        logger.sendFeedback("%d controllers added to this server.".formatted(ControllerManager.INSTANCE.getControllers().size()));
        if (!WhitelistManager.INSTANCE.isNoWhitelist()) {
            WhitelistManager.INSTANCE.forEach(entry -> {
                for (String s : UtilKt.whitelistPrettyPrinting(entry.getValue()).split("\n")) {
                    logger.sendFeedback(s);
                }
                return Unit.INSTANCE;
            });
            logger.sendFeedback("%d whitelists added to this server.".formatted(WhitelistManager.INSTANCE.getAllWhitelist().size()));
        } else {
            logger.sendFeedback("No Whitelist added.");
        }
    }

    public static <T> T fromJson(String content, Class<T> clazz) {
        return gson.fromJson(content, clazz);
    }


    public static String toJson(@NotNull Object obj) {
        return gson.toJson(obj, obj.getClass());
    }

    public static class GlobalExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return false;
        }

        @Override
        public boolean shouldSkipClass(@NotNull Class<?> clazz) {
            return clazz.getName().startsWith("io.") || clazz == ControllerHttpClient.class;
        }
    }

    public static String joinToString(List<String> list, String sep){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1){
                sb.append(sep);
            }
        }
        return sb.toString();
    }

}
