package net.zhuruoling.main

import com.google.gson.Gson
import net.zhuruoling.command.CommandManager.commandTable
import net.zhuruoling.command.CommandManager.registerCommand
import net.zhuruoling.configuration.ConfigReader
import net.zhuruoling.configuration.Configuration
import net.zhuruoling.console.ConsoleHandler
import net.zhuruoling.controller.ControllerManager
import net.zhuruoling.handler.CommandHandlerImpl
import net.zhuruoling.kt.TryKotlin.printOS
import net.zhuruoling.main.RuntimeConstants.noPlugins
import net.zhuruoling.main.RuntimeConstants.test
import net.zhuruoling.network.UdpBroadcastReceiver
import net.zhuruoling.network.UdpBroadcastSender
import net.zhuruoling.network.server.launchHttpServerAsync
import net.zhuruoling.permcode.PermissionManager
import net.zhuruoling.permcode.PermissionManager.calcPermission
import net.zhuruoling.permcode.PermissionManager.getPermission
import net.zhuruoling.permcode.PermissionManager.permissionTable
import net.zhuruoling.plugin.PluginManager
import net.zhuruoling.plugin.PluginManager.loadAll
import net.zhuruoling.plugin.PluginManager.unloadAll
import net.zhuruoling.session.SessionInitialServer
import net.zhuruoling.util.Util
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileLock
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

object MainKt {
    val logger = LoggerFactory.getLogger("Main")
    var config: Configuration? = null
    var isInit = false
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val timeStart = System.currentTimeMillis()
        printOS()
        if (args.size >= 1) {
            val argList = Arrays.stream(args).toList()
            if (argList.contains("--generateExample")) {
                logger.info("Generating examples.")
                Util.generateExample()
                System.exit(0)
            }
            if (argList.contains("--test")) {
                test = true
            }
            if (argList.contains("--noplugin")) {
                noPlugins = true
            }
        }


        if (test) {
            val gson = Gson()
            logger.info(Arrays.toString(gson.fromJson("[\"1\",\"2\",\"3\"]", Array<String>::class.java)))
            logger.info(Util.joinFilePaths("a", "b"))
            PermissionManager.init()
            PluginManager.init()
            loadAll()
            logger.info(permissionTable.toString())
            ControllerManager.init()
            logger.info(getPermission(100860)?.let { calcPermission(it).toString() })
            try {
                throw RuntimeException("Test!")
            } catch (e: RuntimeException) {
                logger.error("An error occurred.", e)
            }
            System.exit(114514)
        }




        logger.info("Hello World!")

        if (!Util.fileExists(Util.getWorkingDir() + File.separator + "config.json")) {
            isInit = true
            Util.createConfig(logger)
        }
        for (folder in Util.DATA_FOLDERS.clone()) {
            val file = File(Util.getWorkingDir() + File.separator + folder)
            if (!file.exists() || !file.isDirectory) isInit = true
        }
        if (isInit) {
            logger.info("Preparing for data folders.")
            for (folder in Util.DATA_FOLDERS.clone()) {
                Util.createFolder(Util.getWorkingDir() + File.separator + folder, logger)
            }
            System.exit(0)
        }
        config = ConfigReader.read()
        if (config == null) {
            logger.error("Empty CONFIG.")
            System.exit(1)
        }
        if (Files.exists(Paths.get(Util.joinFilePaths(Util.LOCK_NAME)))) {
            logger.error("Failed to acquire lock.Might another server instance are running?")
            logger.info(
                "HINT:If you are sure there are no server instance running in this path,you can remove the \"%s\" file. ".formatted(
                    Util.LOCK_NAME
                )
            )
            logger.info("Stopping.")
            System.exit(0)
        }


        val randomAccessFile = RandomAccessFile(Util.joinFilePaths(Util.LOCK_NAME), "rw")
        var lock: FileLock? = null
        try {
            lock = Util.acquireLock(randomAccessFile)
        } catch (e: Exception) {
            logger.error("Failed to acquire lock.Might another server instance are running?")
            logger.info(
                "HINT:If you are sure there are no server instance running in this path,you can remove the \"%s\" file. ".formatted(
                    Util.LOCK_NAME
                )
            )
            logger.info("Stopping.")
            System.exit(3)
            e.printStackTrace()
        }


        Util.listAll(logger)
        try {
            PluginManager.init()
            PermissionManager.init()
            for (command in Util.BUILTIN_COMMANDS.clone()) {
                logger.info("Registering built-in command %s".formatted(command))
                registerCommand(command!!, CommandHandlerImpl())
            }
            loadAll()
        } catch (e: Exception) {
            e.printStackTrace()
            System.exit(2)
        }


        val socketServer = SessionInitialServer()
        socketServer.start()
        val receiver = UdpBroadcastReceiver()
        receiver.start()
        val httpServer = launchHttpServerAsync(args)
        val sender = UdpBroadcastSender()
        sender.start()
        val timeComplete = System.currentTimeMillis()
        val timeUsed = (java.lang.Long.valueOf(timeComplete - timeStart).toString() + ".0f").toFloat() / 1000
        logger.info("Done(%.3fs)! For help, type \"help\" or \"?\"".formatted(timeUsed))
        RuntimeConstants.udpBroadcastSender = sender

        while (true) {
            val scanner = Scanner(System.`in`)
            val line = scanner.nextLine()
            if (line.isBlank()) continue
            logger.info("CONSOLE issued a command:%s".formatted(line))
            if (line == "stop") {
                break
            }
            if (line == "reload") {
                try {
                    unloadAll()
                    logger.debug(commandTable.toString())
                    PluginManager.init()
                    PermissionManager.init()
                    loadAll()
                    continue
                } catch (e: Exception) {
                    logger.error("An error occurred while reloading.", e)
                    continue
                }
            }
            val handler = ConsoleHandler(logger)
            try {
                handler.handle(line)
            } catch (e: RuntimeException) {
                logger.error("An error occurred while parsing commands.", e)
            }
        }

        unloadAll()
        sender.setStopped(true)
        httpServer.interrupt()
        receiver.interrupt()
        socketServer.interrupt()
        logger.info("Releasing lock.")
        Util.releaseLock(lock)
        Files.delete(Path.of(Util.joinFilePaths("omms.lck")))
        logger.info("Stopping.")
        System.exit(0)
    }
}