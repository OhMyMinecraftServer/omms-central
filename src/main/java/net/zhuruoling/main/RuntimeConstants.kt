package net.zhuruoling.main

import net.zhuruoling.configuration.Configuration
import net.zhuruoling.console.PluginCommand
import net.zhuruoling.network.broadcast.UdpBroadcastReceiver
import net.zhuruoling.network.broadcast.UdpBroadcastSender
import net.zhuruoling.network.session.server.SessionInitialServer
import net.zhuruoling.permission.Permission
import java.nio.channels.FileLock
import java.util.StringJoiner

object RuntimeConstants {
    var noLock:Boolean  = false
    var lock: FileLock? = null
    var noPlugins: Boolean = false
    var test: Boolean = false
    var udpBroadcastSender: UdpBroadcastSender? = null
    var launchTime: Long = 0L
    var socketServer: SessionInitialServer? = null
    var httpServer: Thread? = null
    var receiver: UdpBroadcastReceiver? = null
    val permissionNames: MutableList<String> = mutableListOf()
    var config: Configuration? = null
    var normalShutdown: Boolean = false
    var experimental: Boolean = false
    @JvmField
    var pluginCommandHashMap = ArrayList<PluginCommand>()
    init {
        Permission.values().forEach {
            permissionNames.add(it.name)
        }
    }


}