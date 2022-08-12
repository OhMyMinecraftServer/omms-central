package net.zhuruoling.main;

import com.google.gson.Gson;
import net.zhuruoling.broadcast.UdpBroadcastReceiver;
import net.zhuruoling.command.CommandManager;
import net.zhuruoling.configuration.ConfigReader;
import net.zhuruoling.configuration.Configuration;
import net.zhuruoling.console.ConsoleHandler;
import net.zhuruoling.controller.Controller;
import net.zhuruoling.controller.ControllerManager;
import net.zhuruoling.handler.CommandHandlerImpl;
import net.zhuruoling.kt.TryKotlin;
import net.zhuruoling.permcode.PermissionManager;
import net.zhuruoling.plugin.PluginManager;
import net.zhuruoling.server.HttpServerKt;
import net.zhuruoling.session.SessionInitialServer;
import net.zhuruoling.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    final static Logger logger = LoggerFactory.getLogger("Main");

    static Configuration config = null;
    static boolean isInit = false;

    public static void main(String [] args) throws IOException {
        var timeStart = System.currentTimeMillis();
        TryKotlin.INSTANCE.printOS();
        if (args.length >= 1) {
            var argList = Arrays.stream(args).toList();
            if (argList.contains("--generateExample")) {
                logger.info("Generating examples.");
                Util.generateExample();
                System.exit(0);
            }
            if (argList.contains("--test")){
                Flags.INSTANCE.setTest(true);
            }
            if (argList.contains("--noplugin")){
                Flags.INSTANCE.setNoPlugins(true);
            }
        }
        if (Flags.INSTANCE.getTest()){
            Gson gson = new Gson();
            logger.info(Arrays.toString(gson.fromJson("[\"1\",\"2\",\"3\"]", String[].class)));
            logger.info(Util.joinFilePaths("a", "b"));
            PermissionManager.INSTANCE.init();
            PluginManager.INSTANCE.init();
            PluginManager.INSTANCE.loadAll();
            logger.info(PermissionManager.INSTANCE.getPermissionTable().toString());
            ControllerManager.INSTANCE.init();
            logger.info(String.valueOf(PermissionManager.calcPermission(Objects.requireNonNull(PermissionManager.INSTANCE.getPermission(100860)))));
            try {
                throw new RuntimeException("Test!");
            }
            catch (RuntimeException e){
                logger.error("An error occurred.",e);
            }
            System.exit(114514);
        }




        logger.info("Hello World!");

        if (!Util.fileExists(Util.getWorkingDir() + File.separator + "config.json")) {
            isInit = true;
            Util.createConfig(logger);
        }
        for (String folder : Util.DATA_FOLDERS.clone()) {
            File file = new File(Util.getWorkingDir() + File.separator + folder);
            if (!file.exists() || !file.isDirectory())
                isInit = true;
        }


        if (isInit){
            logger.info("Preparing for data folders.");
            for (String folder : Util.DATA_FOLDERS.clone()) {
                Util.createFolder(Util.getWorkingDir() + File.separator + folder, logger);
            }
            System.exit(0);
        }


        config = ConfigReader.read();
        if (config == null){
            logger.error("Empty CONFIG.");
            System.exit(1);
        }
        if (Files.exists(Paths.get(Util.joinFilePaths(Util.LOCK_NAME)))){
            logger.error("Failed to acquire lock.Might another server instance are running?");
            logger.info("HINT:If you are sure there are no server instance running in this path,you can remove the \"%s\" file. ".formatted(Util.LOCK_NAME));
            logger.info("Stopping.");
            System.exit(0);
        }
        var randomAccessFile = new RandomAccessFile(Util.joinFilePaths(Util.LOCK_NAME),"rw");
        FileLock lock = null;
        try {
            lock = Util.acquireLock(randomAccessFile);
        }
        catch (Exception e){
            logger.error("Failed to acquire lock.Might another server instance are running?");
            logger.info("HINT:If you are sure there are no server instance running in this path,you can remove the \"%s\" file. ".formatted(Util.LOCK_NAME));
            logger.info("Stopping.");
            System.exit(3);
            e.printStackTrace();
        }
        Util.listAll(logger);
        try {
            PluginManager.INSTANCE.init();
            PermissionManager.INSTANCE.init();
            for (String command : Util.BUILTIN_COMMANDS.clone()) {
                logger.info("Registering built-in command %s".formatted(command));
                CommandManager.INSTANCE.registerCommand(command,new CommandHandlerImpl());
            }
            PluginManager.INSTANCE.loadAll();
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(2);
        }

        var socketServer = new SessionInitialServer();
        socketServer.start();
        var receiver = new UdpBroadcastReceiver();
        receiver.start();
        var httpServer = HttpServerKt.launchHttpServerAsync(args);
        var timeComplete = System.currentTimeMillis();
        var timeUsed = Float.parseFloat(Long.valueOf(timeComplete - timeStart).toString() + ".0f") / 1000;
        logger.info("Done(%.3fs)! For help, type \"help\" or \"?\"".formatted(timeUsed));
        while (true){
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.isBlank()) continue;
            logger.info("CONSOLE issued a command:%s".formatted(line));
            if (Objects.equals(line, "stop")){
                break;
            }
            if (Objects.equals(line,"reload")){
                try {
                    PluginManager.INSTANCE.unloadAll();
                    logger.debug(CommandManager.INSTANCE.getCommandTable().toString());
                    PluginManager.INSTANCE.init();
                    PermissionManager.INSTANCE.init();
                    PluginManager.INSTANCE.loadAll();
                    continue;
                }
                catch (Exception e){
                    logger.error("An error occurred while reloading.", e);
                    continue;
                }

            }
            ConsoleHandler handler = new ConsoleHandler(logger);
            try {
                handler.handle(line);
            }
            catch (RuntimeException e){
                logger.error("An error occurred while parsing commands.",e);
            }
        }
        PluginManager.INSTANCE.unloadAll();
        httpServer.interrupt();
        receiver.interrupt();
        socketServer.interrupt();
        logger.info("Releasing lock.");
        Util.releaseLock(lock);
        Files.delete(Path.of(Util.joinFilePaths("omms.lck")));
        logger.info("Stopping.");
        System.exit(0);
    }
}
