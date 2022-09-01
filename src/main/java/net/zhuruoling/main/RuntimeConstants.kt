package net.zhuruoling.main

import net.zhuruoling.configuration.Configuration
import net.zhuruoling.network.UdpBroadcastReceiver
import net.zhuruoling.network.UdpBroadcastSender
import net.zhuruoling.permission.Permission
import net.zhuruoling.session.SessionInitialServer
import java.nio.channels.FileLock

object RuntimeConstants {
    var noLock:Boolean  = false
    var lock: FileLock? = null
    var noPlugins: Boolean = false
    var test: Boolean = false
    var udpBroadcastSender: UdpBroadcastSender? = null
    var launchTime: Long = 0L
    var socketServer: SessionInitialServer? = null
    var httpServer: Thread? = null
    var reciever: UdpBroadcastReceiver? = null
    val permissionNames: MutableList<String> = mutableListOf()
    var config: Configuration? = null

    init {
        Permission.values().forEach {
            permissionNames.add(it.name)
        }
    }


}