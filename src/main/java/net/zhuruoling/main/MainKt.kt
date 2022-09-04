package net.zhuruoling.main

import com.google.gson.Gson
import net.zhuruoling.request.RequestManager.registerRequest
import net.zhuruoling.configuration.ConfigReader
import net.zhuruoling.configuration.Configuration
import net.zhuruoling.console.ConsoleHandler
import net.zhuruoling.controller.ControllerManager
import net.zhuruoling.handler.RequestHandlerImpl
import net.zhuruoling.kt.TryKotlin.printOS
import net.zhuruoling.main.RuntimeConstants.noLock
import net.zhuruoling.main.RuntimeConstants.noPlugins
import net.zhuruoling.main.RuntimeConstants.test
import net.zhuruoling.network.UdpBroadcastReceiver
import net.zhuruoling.network.UdpBroadcastSender
import net.zhuruoling.network.server.launchHttpServerAsync
import net.zhuruoling.permission.PermissionManager
import net.zhuruoling.permission.PermissionManager.calcPermission
import net.zhuruoling.permission.PermissionManager.getPermission
import net.zhuruoling.permission.PermissionManager.permissionTable
import net.zhuruoling.plugin.PluginManager
import net.zhuruoling.plugin.PluginManager.loadAll
import net.zhuruoling.session.SessionInitialServer
import net.zhuruoling.util.Util
import org.jline.terminal.TerminalBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileLock
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

object MainKt {
    val logger: Logger = LoggerFactory.getLogger("Main")
    private var config: Configuration? = null
    private var isInit = false

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val timeStart = System.currentTimeMillis()
        RuntimeConstants.launchTime = timeStart;
        printOS()
        if (args.isNotEmpty()) {
            val argList = Arrays.stream(args).toList()
            if (argList.contains("--generateExample")) {
                logger.info("Generating examples.")
                Util.generateExample()
                exitProcess(0)
            }
            if (argList.contains("--test")) {
                test = true
            }
            if (argList.contains("--noplugin")) {
                noPlugins = true
            }
            if (argList.contains("--nolock")) {
                RuntimeConstants.noLock = true
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
            val terminal = TerminalBuilder.builder().system(true).dumb(true).build()
            while (true) {
                val handler0 = ConsoleHandler()
                ConsoleHandler.setLogger(logger)
                handler0.handle(terminal)
            }

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
            exitProcess(0)
        }
        config = ConfigReader.read()
        if (config == null) {
            logger.error("Empty CONFIG.")
            exitProcess(1)
        }
        RuntimeConstants.config = config
        if (Files.exists(Paths.get(Util.joinFilePaths(Util.LOCK_NAME)))) {
            logger.error("Failed to acquire lock.Might another server instance are running?")
            logger.info(
                "HINT:If you are sure there are no server instance running in this path,you can remove the \"%s\" file. ".formatted(
                    Util.LOCK_NAME
                )
            )
            logger.info("Stopping.")
            exitProcess(1)
        }


        if (!noLock) {
            val randomAccessFile = RandomAccessFile(Util.joinFilePaths(Util.LOCK_NAME), "rw")
            val lock: FileLock?
            try {
                lock = Util.acquireLock(randomAccessFile)
                RuntimeConstants.lock = lock
            } catch (e: Exception) {
                logger.error("Failed to acquire lock.Might another server instance are running?")
                logger.info(
                    "HINT:If you are sure there are no server instance running in this path,you can remove the \"%s\" file. ".formatted(
                        Util.LOCK_NAME
                    )
                )
                logger.info("Stopping.")
                exitProcess(3)
            }
        }


        Util.listAll(logger)
        try {
            PluginManager.init()
            PermissionManager.init()
            for (command in Util.BUILTIN_COMMANDS.clone()) {
                logger.info("Registering built-in command %s".formatted(command))
                registerRequest(command!!, RequestHandlerImpl())
            }
            loadAll()
        } catch (e: Exception) {
            e.printStackTrace()
            exitProcess(2)
        }
        Integer.MIN_VALUE


        val socketServer = SessionInitialServer()
        val receiver = UdpBroadcastReceiver()
        val httpServer = launchHttpServerAsync(args)
        val sender = UdpBroadcastSender()
        socketServer.start()
        receiver.start()
        sender.start()
        RuntimeConstants.socketServer = socketServer
        RuntimeConstants.reciever = receiver
        RuntimeConstants.httpServer = httpServer
        RuntimeConstants.udpBroadcastSender = sender
        val timeComplete = System.currentTimeMillis()
        val timeUsed = (java.lang.Long.valueOf(timeComplete - timeStart).toString() + ".0f").toFloat() / 1000
        logger.info("Done(%.3fs)! For help, type \"help\" or \"?\"".formatted(timeUsed))
        RuntimeConstants.udpBroadcastSender = sender

        val terminal = TerminalBuilder.builder().system(true).dumb(true).build()
        while (true) {
            val handler = ConsoleHandler()
            ConsoleHandler.setLogger(logger)
            handler.handle(terminal)
        }
    }
}