package icu.takeneko.omms.central.main

import icu.takeneko.omms.central.RunConfiguration
import icu.takeneko.omms.central.SharedObjects.httpServer
import icu.takeneko.omms.central.SharedObjects.socketServer
import icu.takeneko.omms.central.SharedObjects.udpBroadcastReceiver
import icu.takeneko.omms.central.SharedObjects.udpBroadcastSender
import icu.takeneko.omms.central.State
import icu.takeneko.omms.central.command.CommandManager
import icu.takeneko.omms.central.config.Config
import icu.takeneko.omms.central.console.ConsoleInputHandler
import icu.takeneko.omms.central.controller.ControllerManager
import icu.takeneko.omms.central.foundation.Constants
import icu.takeneko.omms.central.foundation.FeatureOption
import icu.takeneko.omms.central.identity.IdentityProvider
import icu.takeneko.omms.central.network.ChatbridgeImplementation
import icu.takeneko.omms.central.network.chatbridge.UdpBroadcastReceiver
import icu.takeneko.omms.central.network.chatbridge.UdpBroadcastSender
import icu.takeneko.omms.central.network.http.launchHttpServerAsync
import icu.takeneko.omms.central.network.session.request.RequestHandlerManager
import icu.takeneko.omms.central.network.session.server.SessionLoginServer
import icu.takeneko.omms.central.permission.PermissionManager
import icu.takeneko.omms.central.plugin.PluginManager
import icu.takeneko.omms.central.script.ScriptManager
import icu.takeneko.omms.central.util.Util
import icu.takeneko.omms.central.util.printRuntimeEnv
import icu.takeneko.omms.central.whitelist.WhitelistManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.LockSupport
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object CentralServer {
    private val logger: Logger = LoggerFactory.getLogger("Main")
    private val taskQueue = ConcurrentLinkedQueue<() -> Unit>()
    var initialized = false

    fun main(args: Array<String>) {
        val timeStart = System.currentTimeMillis()
        if (args.isNotEmpty()) {
            val argList = Arrays.stream(args).toList()
            RunConfiguration.noPlugins = argList.contains("--noplugin")
        }
        ConsoleInputHandler.INSTANCE.prepareTerminal()
        State.launchTime = timeStart
        printRuntimeEnv()
        logger.info("Hello World!")
        logger.info("Loading Config.")
        if (!Config.load()) {
            exitProcess(1)
        }
        (Constants.DATA_FOLDERS.map { File(Util.getWorkingDirString() + File.separator + it) }
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
            ScriptManager.init()
            ScriptManager.onLoad()
            PermissionManager.init()
            ControllerManager.init()
            WhitelistManager.init()
            IdentityProvider.init()
            CommandManager.INSTANCE.init()
            RequestHandlerManager.init()
        } catch (e: Throwable) {
            logger.error(
                "Looks like OMMS Central Server is not properly configured at current directory, server will not start up until the errors are resolved.",
                e
            )
            exitProcess(2)
        }
        Util.listAll(logger)
        logger.info("Setting up services.")

        val server = SessionLoginServer()
        server.start()
        socketServer = server
        if (config.chatbridgeImplementation == ChatbridgeImplementation.UDP) {
            val rec = UdpBroadcastReceiver()
            rec.start()
            udpBroadcastReceiver = rec
        }

        val thr = launchHttpServerAsync(args)
        httpServer = thr

        if (config.chatbridgeImplementation == ChatbridgeImplementation.UDP) {
            val sender = UdpBroadcastSender()
            sender.start()
            udpBroadcastSender = sender
            udpBroadcastSender.createMulticastSocketCache(Constants.TARGET_CHAT)
        }

        val timeComplete = System.currentTimeMillis()
        val timeUsed = (java.lang.Long.valueOf(timeComplete - timeStart).toString() + ".0f").toFloat() / 1000
        logger.info("Done(${timeUsed}s)! For help, type \"help\".")
        thread(name = "ConsoleThread") {
            while (true) {
                val handler = ConsoleInputHandler.INSTANCE
                handler.handle()
            }
        }
        initialized = true
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

    fun runOnServerThread(fn: () -> Unit) {
        taskQueue.add(fn)
    }

    fun stop() {
        try {
            logger.info("Stopping!")
            if (FeatureOption["script"]) {
                ScriptManager.onUnload()
                ScriptManager.close()
            }
            State.normalShutdown = true
            httpServer.interrupt()
            if (Config.config.chatbridgeImplementation == ChatbridgeImplementation.UDP) {
                udpBroadcastReceiver.interrupt()
                udpBroadcastSender.isStopped = true
            }
            socketServer.interrupt()
            logger.info("Bye")
            if (State.normalShutdown) {
                exitProcess(0)
            }
        } catch (e: java.lang.Exception) {
            logger.error("An exception was thrown while stopping server", e)
        }
    }
}