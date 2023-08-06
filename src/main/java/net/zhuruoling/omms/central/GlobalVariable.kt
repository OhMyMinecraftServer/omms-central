package net.zhuruoling.omms.central

import net.zhuruoling.omms.central.config.Configuration
import net.zhuruoling.omms.central.console.PluginCommand
import net.zhuruoling.omms.central.network.broadcast.UdpBroadcastReceiver
import net.zhuruoling.omms.central.network.broadcast.UdpBroadcastSender
import net.zhuruoling.omms.central.network.session.server.SessionLoginServer
import net.zhuruoling.omms.central.permission.Permission
import org.jline.reader.impl.history.DefaultHistory
import org.slf4j.LoggerFactory
import java.nio.channels.FileLock
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList


object GlobalVariable {
    var noGui: Boolean = false
    var lock: FileLock? = null
    var noPlugins: Boolean = false
    var test: Boolean = false
    var udpBroadcastSender: UdpBroadcastSender? = null
    var launchTime: Long = 0L
    var socketServer: SessionLoginServer? = null
    var httpServer: Thread? = null
    var receiver: UdpBroadcastReceiver? = null
    val permissionNames: MutableList<String> = mutableListOf()
    var config: Configuration? = null
    var normalShutdown: Boolean = false
    var experimental: Boolean = false
    val startupLock = Object()
    val consoleHistory = DefaultHistory()
    val controllerConsoleHistory = hashMapOf<String, DefaultHistory>()
    val logCache = CopyOnWriteArrayList<String>()
    val taskQueue = ConcurrentLinkedQueue<() -> Unit>()
    var args = mutableListOf<String>()
        private set
    fun setArgs(args:Array<String>){
        this.args = args.toMutableList()
    }

    @JvmField
    val publicLogger: org.slf4j.Logger = LoggerFactory.getLogger("PublicLogger")

    @JvmField
    var pluginCommandHashMap = ArrayList<PluginCommand>()

    init {
        Permission.values().forEach {
            permissionNames.add(it.name)
        }
    }


}