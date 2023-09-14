package net.zhuruoling.omms.central

import net.zhuruoling.omms.central.config.Configuration
import net.zhuruoling.omms.central.network.chatbridge.UdpBroadcastReceiver
import net.zhuruoling.omms.central.network.chatbridge.UdpBroadcastSender
import net.zhuruoling.omms.central.network.session.server.SessionLoginServer
import net.zhuruoling.omms.central.permission.Permission
import org.jline.reader.impl.history.DefaultHistory
import java.util.concurrent.CopyOnWriteArrayList


object GlobalVariable {
    var noGui: Boolean = false
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
    val consoleHistory = DefaultHistory()
    val controllerConsoleHistory = hashMapOf<String, DefaultHistory>()
    val logCache = CopyOnWriteArrayList<String>()
    var consoleFontOverride = false
    var consoleFont = "Consolas"
    var args = mutableListOf<String>()
        private set
    fun setArgs(args:Array<String>){
        this.args = args.toMutableList()
    }
    init {
        Permission.values().forEach {
            permissionNames.add(it.name)
        }
    }


}