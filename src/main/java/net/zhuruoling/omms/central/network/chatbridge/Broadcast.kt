package net.zhuruoling.omms.central.network.chatbridge

import com.google.gson.GsonBuilder
import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.config.Config.config
import net.zhuruoling.omms.central.network.ChatbridgeImplementation
import net.zhuruoling.omms.central.network.http.routes.sendToAllWS
import net.zhuruoling.omms.central.util.Util

fun buildFromJson(content: String?): Broadcast? = GsonBuilder().serializeNulls().create().fromJson(content, Broadcast::class.java)

fun buildToJson(broadcast: Broadcast?): String? = GsonBuilder().serializeNulls().create().toJson(broadcast, Broadcast::class.java)

fun sendBroadcast(broadcast: Broadcast) {
    when (config.chatbridgeImplementation) {
        ChatbridgeImplementation.UDP -> GlobalVariable.udpBroadcastSender.addToQueue(
            Util.TARGET_CHAT,
            Util.toJson(broadcast)
        )

        ChatbridgeImplementation.WS -> sendToAllWS(broadcast)
        ChatbridgeImplementation.DISABLE -> {

        }
    }
}

fun buildBroadcast(channel:String, content: String) =  Broadcast().apply {
    setChannel(channel)
    setContent(content)
    setPlayer("********")
    setServer("OMMS CENTRAL")
}