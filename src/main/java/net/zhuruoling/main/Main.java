package net.zhuruoling.main;

import net.zhuruoling.broadcast.UdpBroadcastReceiver;
import net.zhuruoling.command.CommandManager;
import net.zhuruoling.configuration.ConfigReader;
import net.zhuruoling.configuration.Configuration;
import net.zhuruoling.handler.CommandHandlerImpl;
import net.zhuruoling.kt.TryKotlin;
import net.zhuruoling.permcode.PermissionManager;
import net.zhuruoling.plugin.PluginManager;
import net.zhuruoling.server.HttpServer;
import net.zhuruoling.session.SessionInitialServer;
import net.zhuruoling.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Year;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.XMLFormatter;

public class Main {
    final static Logger logger = LoggerFactory.getLogger("Main");

    static Configuration config = null;
    static boolean isInit = false;

    public static void main(String [] args) throws IOException {
        var timeStart = System.currentTimeMillis();
        TryKotlin.INSTANCE.printOS();
        boolean isExampleGen = false;
        if (args.length >= 1) {
            if (Objects.equals(args[0], "--exampleGenerate")){
                logger.info("Generating examples.");
                Util.generateExample();
                System.exit(0);
            }
        }
        boolean test = false;


        if (test){
            logger.info(Util.joinFilePaths("a", "b"));
            PermissionManager.INSTANCE.init();
            PluginManager.INSTANCE.init();
            PluginManager.INSTANCE.loadAll();
            logger.info(PermissionManager.INSTANCE.getPermissionTable().toString());
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
        var httpServerKt = new HttpServer();
        //httpServerKt.start();
        var timeComplete = System.currentTimeMillis();
        var timeUsed = Float.parseFloat(Long.valueOf(timeComplete - timeStart).toString() + ".0f") / 1000;
        logger.info("Done(%.3fs)! For help, type \"help\" or \"?\"".formatted(timeUsed));
        while (true){
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            logger.info("CONSOLE issued a command:%s".formatted(line));
            if (Objects.equals(line, "stop")){
                break;
            }
            if (Objects.equals(line,"reload")){
                PluginManager.INSTANCE.unloadAll();
                logger.debug(CommandManager.INSTANCE.getCommandTable().toString());
                PluginManager.INSTANCE.init();
                PermissionManager.INSTANCE.init();
                PluginManager.INSTANCE.loadAll();
            }
        }
        receiver.interrupt();
        socketServer.interrupt();
        httpServerKt.interrupt();
        System.exit(0);
        //logger.info("Exit.");
    }
}
