package icu.takeneko.omms.central

import icu.takeneko.omms.central.network.chatbridge.UdpBroadcastReceiver
import icu.takeneko.omms.central.network.chatbridge.UdpBroadcastSender
import icu.takeneko.omms.central.network.session.server.SessionLoginServer
import icu.takeneko.omms.central.permission.Permission
import org.jline.reader.impl.history.DefaultHistory
import java.util.concurrent.CopyOnWriteArrayList


object GlobalVariable {
    var noGui: Boolean = false
    var noPlugins: Boolean = false
    var test: Boolean = false
    lateinit var udpBroadcastSender: UdpBroadcastSender
    var launchTime: Long = 0L
    lateinit var socketServer: SessionLoginServer
    lateinit var httpServer: Thread
    lateinit var receiver: UdpBroadcastReceiver
    val permissionNames: MutableList<String> = mutableListOf()
    var normalShutdown: Boolean = false
    var experimental: Boolean = false
    val consoleHistory = DefaultHistory()
    val controllerConsoleHistory = hashMapOf<String, DefaultHistory>()
    val logCache = CopyOnWriteArrayList<String>()
    var consoleFont = "Consolas"
    var args = mutableListOf<String>()
        private set

    fun setArgs(args: Array<String>) {
        GlobalVariable.args = args.toMutableList()
    }

    init {
        Permission.entries.forEach {
            permissionNames.add(it.name)
        }
    }


}