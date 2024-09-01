package icu.takeneko.omms.central.network.chatbridge

import icu.takeneko.omms.central.network.chatbridge.ChatMessageCache.add
import icu.takeneko.omms.central.plugin.callback.ChatbridgeBroadcastReceivedCallback
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket
import java.nio.charset.StandardCharsets
import java.util.*

class UdpBroadcastReceiver : Thread() {
    private val logger: Logger = LoggerFactory.getLogger("UdpBroadcastReceiver")
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    init {
        this.name = "UdpBroadcastReceiver#$id"
    }

    override fun run() {
        println()
        val port = 10086
        val address = "224.114.51.4" // 224.114.51.4:10086
        try {
            val socket = MulticastSocket(port)
            socket.reuseAddress = true
            val inetAddress = InetAddress.getByName(address)
            logger.info("Started Broadcast Receiver at $address:$port")
            socket.joinGroup(InetSocketAddress(inetAddress, port), null)
            val packet = DatagramPacket(ByteArray(1024), 1024)
            while (true) {
                try {
                    socket.receive(packet)
                    val msg = String(
                        packet.data, packet.offset,
                        packet.length, StandardCharsets.UTF_8
                    )
                    val broadcast = json.decodeFromString<Broadcast>(msg)
                    ChatbridgeBroadcastReceivedCallback.INSTANCE.invokeAll(broadcast)
                    add(broadcast)
                    logger.info(
                        "{} <{}[{}]> {}",
                        broadcast.channel,
                        broadcast.player,
                        broadcast.server,
                        broadcast.content
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
