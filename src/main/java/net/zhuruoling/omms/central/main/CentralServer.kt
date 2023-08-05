package net.zhuruoling.omms.central.main

import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.GlobalVariable.experimental
import net.zhuruoling.omms.central.GlobalVariable.httpServer
import net.zhuruoling.omms.central.GlobalVariable.noGui
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
import net.zhuruoling.omms.central.graphics.guiMain
import net.zhuruoling.omms.central.network.ChatbridgeImplementation
import net.zhuruoling.omms.central.network.broadcast.UdpBroadcastReceiver
import net.zhuruoling.omms.central.network.broadcast.UdpBroadcastSender
import net.zhuruoling.omms.central.network.http.launchHttpServerAsync
import net.zhuruoling.omms.central.network.old.session.server.SessionLoginServer
import net.zhuruoling.omms.central.network.session.request.RequestManager
import net.zhuruoling.omms.central.permission.PermissionManager
import net.zhuruoling.omms.central.plugin.PluginManager
import net.zhuruoling.omms.central.util.Util
import net.zhuruoling.omms.central.util.printRuntimeEnv
import net.zhuruoling.omms.central.whitelist.WhitelistManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.locks.LockSupport
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object CentralServer {
    private val logger: Logger = LoggerFactory.getLogger("Main")
    private var config: Configuration? = null
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
        if (!noGui){
            guiMain()
        }
        GlobalVariable.launchTime = timeStart
        printRuntimeEnv()

        logger.info("Hello World!")
        logger.info("Loading Config.")
        (Arrays.stream(Util.DATA_FOLDERS).map { File(Util.getWorkingDir() + File.separator + it) }
            .filter { !(it.isDirectory or it.exists()) }.toList() to
                !Util.fileExists(Util.getWorkingDir() + File.separator + "config.json")).run {
            first.run {
                if (isNotEmpty()) {
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
        logger.info("Setting up managers.")
        try {
            PluginManager.init()
            PluginManager.loadAll()
            PermissionManager.init()
            ControllerManager.init()
            AnnouncementManager.init()
            WhitelistManager.init()
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
        thread(name = "ConsoleThread"){
            while (true) {
                val handler = ConsoleInputHandler.INSTANCE
                handler.handle()
            }
        }
        while (true){
            while (GlobalVariable.taskQueue.isNotEmpty()) {
                try{
                    GlobalVariable.taskQueue.poll()()
                }catch (e:Exception){
                    logger.error("Error occurred while executing tasks",e)
                }
            }
            LockSupport.parkNanos(10)
        }
    }

    fun runOnMainThread(fn: () -> Unit){
        GlobalVariable.taskQueue.add(fn)
    }

    @JvmStatic
    fun stop() {
        if (test) exitProcess(0)
        try {
            logger.info("Stopping!")
            normalShutdown = true
            Objects.requireNonNull(httpServer)?.interrupt()
            if (GlobalVariable.config?.chatbridgeImplementation == ChatbridgeImplementation.UDP) {
                Objects.requireNonNull(receiver)?.interrupt()
                Objects.requireNonNull(udpBroadcastSender)?.isStopped = true
            }
            Objects.requireNonNull(socketServer)?.interrupt()
            logger.info("Bye")
            if (normalShutdown) {
                exitProcess(0)
            }
        } catch (e: java.lang.Exception) {
            logger.error("Cannot stop server.", e)
        }

    }
}