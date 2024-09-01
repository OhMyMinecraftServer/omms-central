package icu.takeneko.omms.central.network.chatbridge

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.LockSupport

class UdpBroadcastSender : Thread() {
    private val logger: Logger = LoggerFactory.getLogger("UdpBroadcastSender")
    var isStopped: Boolean = false
    private val queue = LinkedBlockingQueue<Pair<UdpBroadcastTarget, ByteArray>>()
    private val multicastSocketCache = mutableMapOf<UdpBroadcastTarget, MulticastSocket>()

    init {
        this.name = "UdpBroadcastSender#" + this.id
    }

    fun createMulticastSocketCache(target: UdpBroadcastTarget) {
        try {
            multicastSocketCache[target] = createMulticastSocket(target.address, target.port)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun clearMulticastSocketCache() {
        multicastSocketCache.clear()
    }

    override fun run() {
        logger.info("Starting UdpBroadcastSender.")
        while (!isStopped) {
            try {
                while (queue.isNotEmpty()) {
                    val (t, b) = queue.poll()
                    this.send(t, b)
                }
                LockSupport.parkNanos(1000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        logger.info("Stopping!")
    }

    fun addToQueue(target: UdpBroadcastTarget, content: String) {
        queue.add(target to content.toByteArray(StandardCharsets.UTF_8))
    }

    private fun send(target: UdpBroadcastTarget, content: ByteArray) {
        try {
            val socket = if (multicastSocketCache.containsKey(target)) {
                multicastSocketCache[target]!!
            } else {
                createMulticastSocket(target.address, target.port).also { multicastSocketCache[target] = it }
            }
            val packet = DatagramPacket(content, content.size, InetAddress.getByName(target.address), target.port)
            socket.send(packet)
        } catch (e: Exception) {
            logger.error("Send UDP Broadcast failed, target={}, content={}", target, content)
        }
    }

    private fun createMulticastSocket(addr: String, port: Int): MulticastSocket {
        val socket = MulticastSocket(port)
        val inetAddress = InetAddress.getByName(addr)
        socket.joinGroup(InetSocketAddress(addr, port), NetworkInterface.getByInetAddress(inetAddress))
        return socket
    }
}