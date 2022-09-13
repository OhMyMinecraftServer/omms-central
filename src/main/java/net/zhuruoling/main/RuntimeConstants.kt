package net.zhuruoling.main

import net.zhuruoling.configuration.Configuration
import net.zhuruoling.network.broadcast.UdpBroadcastReceiver
import net.zhuruoling.network.broadcast.UdpBroadcastSender
import net.zhuruoling.permission.Permission
import net.zhuruoling.network.session.server.SessionInitialServer
import java.nio.channels.FileLock

object RuntimeConstants {
    var noLock:Boolean  = false
    var lock: FileLock? = null
    var noPlugins: Boolean = false
    var test: Boolean = false
    var udpBroadcastSender: net.zhuruoling.network.broadcast.UdpBroadcastSender? = null
    var launchTime: Long = 0L
    var socketServer: SessionInitialServer? = null
    var httpServer: Thread? = null
    var reciever: net.zhuruoling.network.broadcast.UdpBroadcastReceiver? = null
    val permissionNames: MutableList<String> = mutableListOf()
    var config: Configuration? = null
    var normalShutdown: Boolean = false

    init {
        Permission.values().forEach {
            permissionNames.add(it.name)
        }
    }


}