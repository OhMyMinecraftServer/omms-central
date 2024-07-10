package icu.takeneko.omms.central

import icu.takeneko.omms.central.network.chatbridge.UdpBroadcastReceiver
import icu.takeneko.omms.central.network.chatbridge.UdpBroadcastSender
import icu.takeneko.omms.central.network.session.server.SessionLoginServer
import org.jline.reader.impl.history.DefaultHistory
import java.util.concurrent.CopyOnWriteArrayList

object RunConfiguration{
    var noGui: Boolean = false
    var noPlugins: Boolean = false
    var consoleFont = "Consolas"
    val args = mutableListOf<String>()
}

object State{
    var normalShutdown: Boolean = false
    var launchTime: Long = 0L
}

object SharedObjects{
    lateinit var socketServer: SessionLoginServer
    lateinit var httpServer: Thread
    lateinit var udpBroadcastReceiver: UdpBroadcastReceiver
    lateinit var udpBroadcastSender: UdpBroadcastSender
    val logCache = CopyOnWriteArrayList<String>()
    val consoleHistory = DefaultHistory()
    val controllerConsoleHistory = hashMapOf<String, DefaultHistory>()
}

