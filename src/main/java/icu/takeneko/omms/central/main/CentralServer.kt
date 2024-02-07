package icu.takeneko.omms.central.main

import icu.takeneko.omms.central.GlobalVariable
import icu.takeneko.omms.central.GlobalVariable.experimental
import icu.takeneko.omms.central.GlobalVariable.httpServer
import icu.takeneko.omms.central.GlobalVariable.noGui
import icu.takeneko.omms.central.GlobalVariable.noPlugins
import icu.takeneko.omms.central.GlobalVariable.normalShutdown
import icu.takeneko.omms.central.GlobalVariable.receiver
import icu.takeneko.omms.central.GlobalVariable.socketServer
import icu.takeneko.omms.central.GlobalVariable.test
import icu.takeneko.omms.central.GlobalVariable.udpBroadcastSender
import icu.takeneko.omms.central.announcement.AnnouncementManager
import icu.takeneko.omms.central.command.CommandManager
import icu.takeneko.omms.central.config.Config
import icu.takeneko.omms.central.console.ConsoleInputHandler
import icu.takeneko.omms.central.controller.ControllerManager
import icu.takeneko.omms.central.graphics.guiMain
import icu.takeneko.omms.central.identity.IdentityProvider
import icu.takeneko.omms.central.network.ChatbridgeImplementation
import icu.takeneko.omms.central.network.chatbridge.UdpBroadcastReceiver
import icu.takeneko.omms.central.network.chatbridge.UdpBroadcastSender
import icu.takeneko.omms.central.network.http.launchHttpServerAsync
import icu.takeneko.omms.central.network.session.server.SessionLoginServer
import icu.takeneko.omms.central.network.session.request.RequestManager
import icu.takeneko.omms.central.permission.PermissionManager
import icu.takeneko.omms.central.plugin.PluginManager
import icu.takeneko.omms.central.util.Util
import icu.takeneko.omms.central.util.printRuntimeEnv
import icu.takeneko.omms.central.whitelist.WhitelistManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.LockSupport
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object CentralServer {
    private val logger: Logger = LoggerFactory.getLogger("Main")
    private val taskQueue = ConcurrentLinkedQueue<() -> Unit>()
    var initialized = false

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val timeStart = System.currentTimeMillis()
        if (args.isNotEmpty()) {
            val argList = Arrays.stream(args).toList()
            noPlugins = argList.contains("--noplugin")
            experimental = argList.contains("--experimental")
            noGui = argList.contains("--nogui")
        }
        ConsoleInputHandler.INSTANCE.prepareTerminal()
        if (!noGui) {
            guiMain()
        }
        GlobalVariable.launchTime = timeStart
        printRuntimeEnv()

        logger.info("Hello World!")
        logger.info("Loading Config.")
        (Util.DATA_FOLDERS.map { File(Util.getWorkingDir() + File.separator + it) }
            .filter { !(it.isDirectory or it.exists()) }.toList())
            .run {
                if (isNotEmpty()) {
                    logger.info("Preparing data folders.")
                    forEach {
                        Util.createFolder(it.path, logger)
                    }
                }
                if (isNotEmpty()) exitProcess(0)
            }
        if (!Config.load()) {
            exitProcess(1)
        }
        val config = Config.config
        logger.info("Config:")
        logger.info("\tServerName: ${config.serverName}")
        logger.info("\tSocketPort: ${config.port}")
        logger.info("\tHttpPort: ${config.httpPort}")
        logger.info("\tAuthorisedController: ${config.authorisedController}")
        logger.info("\tRequestRateLimit: ${config.rateLimit}")
        logger.info("\tChatbridgeImplementation: ${config.chatbridgeImplementation}")
        logger.info("\tApiAccessKey: ${config.apiAccessKey.substring(0..5) + "*".repeat(26)}")
        logger.info("Setting up managers.")
        try {
            PluginManager.init()
            PluginManager.loadAll()
            PermissionManager.init()
            ControllerManager.init()
            AnnouncementManager.init()
            WhitelistManager.init()
            IdentityProvider.init()
            CommandManager.INSTANCE.init()
            RequestManager.init()
        } catch (e: Exception) {
            e.printStackTrace()
            exitProcess(2)
        }
        Util.listAll(logger)
        logger.info("Setting up services.")

        val socketServer = SessionLoginServer()
        socketServer.start()
        GlobalVariable.socketServer = socketServer
        if (config.chatbridgeImplementation == ChatbridgeImplementation.UDP) {
            val receiver = UdpBroadcastReceiver()
            receiver.start()
            GlobalVariable.receiver = receiver
        }

        val httpServer = launchHttpServerAsync(args)
        GlobalVariable.httpServer = httpServer

        if (config.chatbridgeImplementation == ChatbridgeImplementation.UDP) {
            val sender = UdpBroadcastSender()
            sender.start()
            udpBroadcastSender = sender
            udpBroadcastSender.createMulticastSocketCache(Util.TARGET_CHAT)
        }

        val timeComplete = System.currentTimeMillis()
        val timeUsed = (java.lang.Long.valueOf(timeComplete - timeStart).toString() + ".0f").toFloat() / 1000
        logger.info("Done(${timeUsed}s)! For help, type \"help\".")
        initialized = true
        thread(name = "ConsoleThread") {
            while (true) {
                val handler = ConsoleInputHandler.INSTANCE
                handler.handle()
            }
        }
        while (true) {
            while (taskQueue.isNotEmpty()) {
                try {
                    taskQueue.poll()()
                } catch (e: Exception) {
                    logger.error("Error occurred while executing tasks", e)
                }
            }
            LockSupport.parkNanos(10)
        }
    }

    fun runOnMainThread(fn: () -> Unit) {
        taskQueue.add(fn)
    }

    @JvmStatic
    fun stop() {
        if (test) exitProcess(0)
        try {
            logger.info("Stopping!")
            normalShutdown = true
            httpServer.interrupt()
            if (Config.config.chatbridgeImplementation == ChatbridgeImplementation.UDP) {
                receiver.interrupt()
                udpBroadcastSender.isStopped = true
            }
            socketServer.interrupt()
            logger.info("Bye")
            if (normalShutdown) {
                exitProcess(0)
            }
        } catch (e: java.lang.Exception) {
            logger.error("Cannot stop server.", e)
        }
    }
}