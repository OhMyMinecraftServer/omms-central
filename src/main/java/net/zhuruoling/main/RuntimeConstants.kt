package net.zhuruoling.main

import net.zhuruoling.network.UdpBroadcastReceiver
import net.zhuruoling.network.UdpBroadcastSender
import net.zhuruoling.session.SessionInitialServer
import java.nio.channels.FileLock

object RuntimeConstants {
    var lock: FileLock? = null
    var noPlugins: Boolean = false
    var test: Boolean = false
    var udpBroadcastSender: UdpBroadcastSender? = null
    var launchTime: Long = 0L
    var socketServer: SessionInitialServer? = null
    var httpServer: Thread? = null
    var reciever: UdpBroadcastReceiver? = null
}