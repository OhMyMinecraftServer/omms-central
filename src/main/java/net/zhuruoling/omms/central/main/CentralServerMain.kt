package net.zhuruoling.omms.central.main

import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.GlobalVariable.experimental
import net.zhuruoling.omms.central.GlobalVariable.httpServer
import net.zhuruoling.omms.central.GlobalVariable.lock
import net.zhuruoling.omms.central.GlobalVariable.noLock
import net.zhuruoling.omms.central.GlobalVariable.noPlugins
import net.zhuruoling.omms.central.GlobalVariable.normalShutdown
import net.zhuruoling.omms.central.GlobalVariable.receiver
import net.zhuruoling.omms.central.GlobalVariable.socketServer
import net.zhuruoling.omms.central.GlobalVariable.test
import net.zhuruoling.omms.central.GlobalVariable.udpBroadcastSender
import net.zhuruoling.omms.central.announcement.AnnouncementManager
import net.zhuruoling.omms.central.command.CommandManager
import net.zhuruoling.omms.central.config.ConfigReader
import net.zhuruoling.omms.central.config.Configuration
import net.zhuruoling.omms.central.console.ConsoleInputHandler
import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.network.ChatbridgeImplementation
import net.zhuruoling.omms.central.network.broadcast.UdpBroadcastReceiver
import net.zhuruoling.omms.central.network.broadcast.UdpBroadcastSender
import net.zhuruoling.omms.central.network.http.launchHttpServerAsync
import net.zhuruoling.omms.central.network.session.request.RequestManager
import net.zhuruoling.omms.central.network.session.server.SessionInitialServer
import net.zhuruoling.omms.central.permission.PermissionManager
import net.zhuruoling.omms.central.plugin.PluginManager
import net.zhuruoling.omms.central.script.ScriptManager
import net.zhuruoling.omms.central.util.Util
import net.zhuruoling.omms.central.util.printRuntimeEnv
import net.zhuruoling.omms.central.whitelist.WhitelistManager
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

object CentralServerMain {
    private val logger: Logger = LoggerFactory.getLogger("Main")
    private var config: Configuration? = null
    var initialized = false

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        ConsoleInputHandler.INSTANCE.prepareTerminal()
        val timeStart = System.currentTimeMillis()
        GlobalVariable.launchTime = timeStart
        printRuntimeEnv()
        if (args.isNotEmpty()) {
            val argList = Arrays.stream(args).toList()
            noPlugins = argList.contains("--noplugin")
            noLock = argList.contains("--nolock")
            experimental = argList.contains("--experimental")
            GlobalVariable.noScripts = argList.contains("--noscripts")
        }

        logger.info("Hello World!")
        logger.info("Loading Config.")

        (Arrays.stream(Util.DATA_FOLDERS).map { File(Util.getWorkingDir() + File.separator + it) }
            .filter { !(it.isDirectory or it.exists()) }.toList() to
                !Util.fileExists(Util.getWorkingDir() + File.separator + "config.json")).run {
            first.run {
                if (!isEmpty()) {
                    logger.info("Preparing for data folders.")
                    forEach {
                        Util.createFolder(it.path, logger)
                    }
                }
            }
            if (second) {
                Util.createConfig(logger)
            }
            if (first.isNotEmpty() or second) exitProcess(0)
        }

        config = ConfigReader.read()
        if (config == null) {
            logger.error("Empty CONFIG.")
            exitProcess(1)
        }
        GlobalVariable.config = config
        logger.info("Config:")
        logger.info("\tServerName: ${config?.serverName}")
        logger.info("\tSocketPort: ${config?.port}")
        logger.info("\tHttpPort: ${config?.httpPort}")
        logger.info("\tAuthorisedController: ${Arrays.toString(config?.authorisedController)}")
        logger.info("\tRequestRateLimit: ${config?.rateLimit}")
        logger.info("\tChatbridgeImplementation: ${config?.chatbridgeImplementation}")

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
                GlobalVariable.lock = lock
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
            PluginManager.loadAll()
            ScriptManager.init()
            PermissionManager.init()
            ControllerManager.init()
            AnnouncementManager.init()
            WhitelistManager.init()
            CommandManager.INSTANCE.init()
            RequestManager.init()
            ScriptManager.loadAll()
        } catch (e: Exception) {
            e.printStackTrace()
            exitProcess(2)
        }
        Util.listAll(logger)
        logger.info("Setting up services.")

        val socketServer = SessionInitialServer()
        socketServer.start()
        GlobalVariable.socketServer = socketServer
        if (GlobalVariable.config!!.chatbridgeImplementation == ChatbridgeImplementation.UDP) {
            val receiver = UdpBroadcastReceiver()
            receiver.start()
            GlobalVariable.receiver = receiver
        }

        val httpServer = launchHttpServerAsync(args)
        GlobalVariable.httpServer = httpServer

        if (GlobalVariable.config!!.chatbridgeImplementation == ChatbridgeImplementation.UDP){
            val sender = UdpBroadcastSender()
            sender.start()
            udpBroadcastSender = sender
            udpBroadcastSender?.createMulticastSocketCache(Util.TARGET_CHAT)
        }

        val timeComplete = System.currentTimeMillis()
        val timeUsed = (java.lang.Long.valueOf(timeComplete - timeStart).toString() + ".0f").toFloat() / 1000
        logger.info("Done(${timeUsed}s)! For help, type \"help\".")
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
            ScriptManager.unloadAll()
            Objects.requireNonNull(httpServer)?.interrupt()
            if (GlobalVariable.config?.chatbridgeImplementation == ChatbridgeImplementation.UDP) {
                Objects.requireNonNull(receiver)?.interrupt()
                Objects.requireNonNull(udpBroadcastSender)?.isStopped = true
            }
            Objects.requireNonNull(socketServer)?.interrupt()
            if (!noLock) {
                logger.info("Releasing lock.")
                Util.releaseLock(lock!!)
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