package icu.takeneko.omms.central.network.chatbridge

import icu.takeneko.omms.central.network.session.server.SessionServer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ChatMessageCache {
    private val list = mutableListOf<Broadcast>()
    private var maxCapacity: Int = 50

    fun add(broadcast: Broadcast) {
        SessionServer.sessions.forEach {
            it.sendBroadcastMessage(broadcast)
        }
        synchronized(list) {
            if (list.size + 1 > maxCapacity) {
                list.removeAt(0)
                list.add(broadcast)
            } else {
                list.add(broadcast)
            }
        }
    }

    fun updateMaxCapacity(newValue: Int) {
        if (newValue < 1) throw IllegalArgumentException("maxCapacity < 1")
        synchronized(list) { maxCapacity = newValue }
    }

    operator fun plusAssign(broadcast: Broadcast) = add(broadcast)

    fun toMessageCache():MessageCache{
        return MessageCache(maxCapacity, list.map(Message::fromBroadcast))
    }

    fun serialize():String = Json.encodeToString<MessageCache>(toMessageCache())

    @Serializable
    data class MessageCache(val maxCapacity: Int, val messages: List<Message>)

    @Serializable
    data class Message(
        val channel: String,
        val server: String,
        val player: String,
        val content: String,
        val id: String,
    ) {
        companion object {
            fun fromBroadcast(broadcast: Broadcast): Message {
                return Message(broadcast.channel, broadcast.server, broadcast.player, broadcast.content, broadcast.id)
            }
        }
    }
}