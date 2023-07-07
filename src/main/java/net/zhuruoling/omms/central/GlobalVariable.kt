package net.zhuruoling.omms.central

import net.zhuruoling.omms.central.config.Configuration
import net.zhuruoling.omms.central.console.PluginCommand
import net.zhuruoling.omms.central.network.broadcast.UdpBroadcastReceiver
import net.zhuruoling.omms.central.network.broadcast.UdpBroadcastSender
import net.zhuruoling.omms.central.network.session.server.SessionLoginServer
import net.zhuruoling.omms.central.permission.Permission
import org.jline.reader.impl.history.DefaultHistory
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.nio.channels.FileLock


object GlobalVariable {
    var noLock: Boolean = false
    var lock: FileLock? = null
    var noPlugins: Boolean = false
    var noScripts: Boolean = false
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
    val pluginDeclaredApiMethod = hashMapOf<String, HashMap<String, Method>>()

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