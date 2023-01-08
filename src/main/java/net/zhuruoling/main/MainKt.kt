package net.zhuruoling.main

import com.google.gson.Gson
import net.zhuruoling.announcement.AnnouncementManager
import net.zhuruoling.configuration.ConfigReader
import net.zhuruoling.configuration.Configuration
import net.zhuruoling.console.ConsoleInputHandler
import net.zhuruoling.controller.ControllerManager
import net.zhuruoling.foo.Foo.bar
import net.zhuruoling.main.RuntimeConstants.experimental
import net.zhuruoling.main.RuntimeConstants.httpServer
import net.zhuruoling.main.RuntimeConstants.lock
import net.zhuruoling.main.RuntimeConstants.noLock
import net.zhuruoling.main.RuntimeConstants.noPlugins
import net.zhuruoling.main.RuntimeConstants.normalShutdown
import net.zhuruoling.main.RuntimeConstants.receiver
import net.zhuruoling.main.RuntimeConstants.socketServer
import net.zhuruoling.main.RuntimeConstants.startupLock
import net.zhuruoling.main.RuntimeConstants.test
import net.zhuruoling.main.RuntimeConstants.udpBroadcastSender
import net.zhuruoling.network.broadcast.UdpBroadcastReceiver
import net.zhuruoling.network.broadcast.UdpBroadcastSender
import net.zhuruoling.network.http.launchHttpServerAsync
import net.zhuruoling.network.session.handler.builtin.registerBuiltinRequestHandlers

import net.zhuruoling.network.session.server.SessionInitialServer
import net.zhuruoling.permission.PermissionManager
import net.zhuruoling.permission.PermissionManager.calcPermission
import net.zhuruoling.permission.PermissionManager.getPermission
import net.zhuruoling.permission.PermissionManager.permissionTable
import net.zhuruoling.plugin.PluginManager
import net.zhuruoling.plugin.PluginManager.loadAll
import net.zhuruoling.plugin.PluginManager.unloadAll
import net.zhuruoling.util.Util
import net.zhuruoling.whitelist.WhitelistManager
import org.jline.terminal.TerminalBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileLock
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

object MainKt {
    private val logger: Logger = LoggerFactory.getLogger("Main")
    private var config: Configuration? = null
    private var isInit = false
    var initialized = false

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val timeStart = System.currentTimeMillis()
        RuntimeConstants.launchTime = timeStart
        bar()

        if (args.isNotEmpty()) {
            val argList = Arrays.stream(args).toList()
            if (argList.contains("--generateExample")) {
                logger.info("Generating examples.")
                Util.generateExample()
                exitProcess(0)
            }
            test = argList.contains("--test")
            noPlugins = argList.contains("--noplugin")
            noLock = argList.contains("--nolock")
            experimental = argList.contains("--experimental")
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
                logger.error("An error occurred.")
                e.printStackTrace()
            }


            while (true) {
                val handler0 = ConsoleInputHandler.INSTANCE
                handler0.handle()
            }

        }

        logger.info("Hello World!")
        logger.info("Loading Config.")
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
        println("Config:")
        println("\tServerName: ${config?.serverName}")
        println("\tSocketPort: ${config?.port}")
        println("\tHttpPort: ${config?.httpPort}")
        println("\tAuthorisedController: ${Arrays.toString(config?.authorisedController)}")

        if (Files.exists(Paths.get(Util.joinFilePaths(Util.LOCK_NAME)))) {
            logger.error("Failed to acquire lock.Might another server instance are running?")
            logger.info("HINT:If you are sure there are no server instance running in this path,you can remove the \"${Util.LOCK_NAME}\" file. ")
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
                logger.info("HINT:If you are sure there are no server instance running in this path,you can remove the \"${Util.LOCK_NAME}\" file. ")
                logger.info("Stopping.")
                exitProcess(3)
            }
        }

        logger.info("Setting up managers.")
        try {
            PluginManager.init()
            PermissionManager.init()
            ControllerManager.init()
            AnnouncementManager.init()
            WhitelistManager.init()
            registerBuiltinRequestHandlers()
            loadAll()

        } catch (e: Exception) {
            e.printStackTrace()
            exitProcess(2)
        }
        Util.listAll(logger)
        logger.info("Setting up services.")
        val socketServer = SessionInitialServer()
        val receiver = UdpBroadcastReceiver()
        val httpServer = launchHttpServerAsync(args)
        val sender = UdpBroadcastSender()
        socketServer.start()
        receiver.start()
        sender.start()
        RuntimeConstants.socketServer = socketServer
        RuntimeConstants.receiver = receiver
        RuntimeConstants.httpServer = httpServer
        udpBroadcastSender = sender
        udpBroadcastSender?.createMulticastSocketCache(Util.TARGET_CONTROL)
        udpBroadcastSender?.createMulticastSocketCache(Util.TARGET_CHAT)
        val timeComplete = System.currentTimeMillis()
        val timeUsed = (java.lang.Long.valueOf(timeComplete - timeStart).toString() + ".0f").toFloat() / 1000
        logger.info("Done(${timeUsed}s)! For help, type \"help\" or \"?\"")
        udpBroadcastSender = sender
        initialized = true
        while (true) {
            val handler = ConsoleInputHandler.INSTANCE
            handler.handle()
        }
    }

    @JvmStatic
    fun stop() {
        if (test) exitProcess(0)
        try {
            logger.info("Stopping!")
            normalShutdown = true
            unloadAll()
            Objects.requireNonNull(httpServer)?.interrupt()
            Objects.requireNonNull(receiver)?.interrupt()
            Objects.requireNonNull(udpBroadcastSender)?.isStopped = true
            Objects.requireNonNull(socketServer)?.interrupt()
            if (!noLock) {
                logger.info("Releasing lock.")
                Util.releaseLock(lock)
                Files.delete(Path.of(Util.LOCK_NAME))
            }
            logger.info("Bye")
            if (normalShutdown) {
                exitProcess(0)
            }
        } catch (e: java.lang.Exception) {
            logger.error("Cannot stop server.", e)
        }

    }
}