package icu.takeneko.omms.central.network.http.routes

import icu.takeneko.omms.central.network.chatbridge.Broadcast
import icu.takeneko.omms.central.network.chatbridge.ChatMessageCache
import icu.takeneko.omms.central.plugin.callback.ChatbridgeBroadcastReceivedCallback
import icu.takeneko.omms.central.util.Util
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.utils.io.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

private val list = mutableListOf<DefaultWebSocketSession>()
private val logger = LoggerFactory.getLogger("WebsocketRoute")

fun Broadcast.sendToAllWS() {
    runBlocking {
        synchronized(list) {
            for (session in list) {
                launch(Dispatchers.IO) {
                    session.send(this@sendToAllWS.toJson())
                }
            }
        }
    }
}

fun Route.websocketRoute() {
    route("/chatbridge") {
        webSocket {
            logger.debug("Websocket chat client ${Integer.toHexString(this.hashCode())} connected")
            list += this
            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val received = frame.readText()
                    if (received == "PING") this.send("PONG@${System.currentTimeMillis()}")
                    try {
                        val broadcast = Util.fromJson(received, Broadcast::class.java)
                        logger.info("[${broadcast.channel}] [${broadcast.server}] <${broadcast.player}> ${broadcast.content}")
                        ChatbridgeBroadcastReceivedCallback.INSTANCE.invokeAll(broadcast)
                        ChatMessageCache += broadcast
                        synchronized(list) {
                            for (session in list) {
                                if (session == this) continue
                                launch(Dispatchers.IO) {
                                    session.send(received)
                                }
                            }
                        }
                    } catch (_: CancellationException) {
                        continue
                    } catch (e: Exception) {
                        logger.warn("Cannot decode chat message:$e")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: CancellationException) {
                logger.debug("Websocket chat client ${Integer.toHexString(this.hashCode())} disconnected")
                return@webSocket
            } finally {
                logger.debug("Websocket chat client ${Integer.toHexString(this.hashCode())} disconnected")
            }
        }
    }
}