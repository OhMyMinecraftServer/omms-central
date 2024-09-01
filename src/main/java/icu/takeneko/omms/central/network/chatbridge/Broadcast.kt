package icu.takeneko.omms.central.network.chatbridge

import icu.takeneko.omms.central.SharedObjects
import icu.takeneko.omms.central.config.Config.config
import icu.takeneko.omms.central.fundation.Constants
import icu.takeneko.omms.central.network.ChatbridgeImplementation
import icu.takeneko.omms.central.network.http.routes.sendToAllWS
import icu.takeneko.omms.central.util.Util
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

@kotlinx.serialization.Serializable
data class Broadcast(
    val channel: String,
    val server: String,
    val player: String,
    val content: String,
    val id: String = Util.generateRandomString(16),
    val timeMillis: Long = System.currentTimeMillis()
) {
    fun toJson(): String = json.encodeToString(this)

    constructor(channel: String, content: String) : this(
        channel,
        "OMMS CENTRAL",
        "",
        content
    )
}

fun Broadcast.send() {
    if (config.chatbridgeImplementation != ChatbridgeImplementation.DISABLE) {
        ChatMessageCache += this
    }
    when (config.chatbridgeImplementation) {
        ChatbridgeImplementation.UDP -> SharedObjects.udpBroadcastSender.addToQueue(
            Constants.TARGET_CHAT,
            this.toJson()
        )

        ChatbridgeImplementation.WS -> this.sendToAllWS()
        ChatbridgeImplementation.DISABLE -> {

        }
    }
}

fun chatbridgeAvailable() = config.chatbridgeImplementation != ChatbridgeImplementation.DISABLE