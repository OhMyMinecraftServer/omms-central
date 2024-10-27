package icu.takeneko.omms.central.controller.console.ws

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import icu.takeneko.omms.central.controller.ControllerImpl
import icu.takeneko.omms.central.controller.console.ws.packet.WSCommandPacket
import icu.takeneko.omms.central.controller.console.ws.packet.WSCompletionRequestPacket
import icu.takeneko.omms.central.controller.console.ws.packet.WSPacket
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.LockSupport

class ControllerWebSocketSession(
    private val controllerImpl: ControllerImpl,
    private val handler: WSPacketHandler
) : Thread("Console@${controllerImpl.name}") {
    private val logger = LoggerFactory.getLogger("ControllerWSConsoleImpl")
    private val cache = mutableListOf<WSPacket>()
    private val completionCallback = mutableMapOf<String, CompletableFuture<List<String>>>()
    var connected = AtomicBoolean(false)
    private val client = HttpClient(CIO) {
        install(WebSockets)
        engine {
            threadsCount = 4
            pipelining = true
        }
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username = controllerImpl.name, password = controllerImpl.name.encodeBase64())
                }
                realm = "Access to the client"
            }
        }
    }

    override fun run() {
        try {
            runBlocking {
                try {
                    val sp = controllerImpl.httpQueryAddress.split(":")
                    client.webSocket(method = HttpMethod.Get, host = sp[0], port = sp[1].toInt(), path = "/") {
                        try {
                            connected.set(true)
                            val output = launch(Dispatchers.IO) {
                                for (line in incoming) {
                                    line as? Frame.Text ?: continue
                                    val s = line.readText()
                                    launch {
                                        try {
                                            logger.debug("Incoming message: $s")
                                            decodePacket(s).handle(handler)
                                        } catch (e: Exception) {
                                            logger.error("Message parse failed:", e)
                                        }
                                    }
                                }
                            }
                            val input = launch(Dispatchers.IO) {
                                while (true) {
                                    synchronized(cache) {
                                        if (cache.isNotEmpty()) {
                                            for (s in cache) {
                                                runBlocking {
                                                    try {
                                                        val e = encodePacket(s)
                                                        logger.debug("Sending $e")
                                                        this@webSocket.send(e)
                                                    } catch (e: Exception) {
                                                        logger.warn("Send packet $s failed.", e)
                                                    }
                                                }
                                            }
                                            cache.clear()
                                        }
                                    }
                                    LockSupport.parkNanos(1000)
                                }
                            }
                            input.join()
                            output.cancelAndJoin()
                        } catch (_: InterruptedException) {
                        }
                    }
                } catch (_: InterruptedException) {
                } catch (e: Exception) {
                    e.printStackTrace()
                    connected.set(false)
                    client.close()
                    return@runBlocking
                }
            }
        } catch (_: InterruptedException) {
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun packet(packet: WSPacket) {
        synchronized(cache) {
            cache.add(packet)
        }
    }

    fun command(line: String) {
        synchronized(cache) {
            cache.add(WSCommandPacket(line))
        }
    }

    private fun decodePacket(content:String):WSPacket {
        return WSPacket.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(content)).orThrow.first
    }

    private fun encodePacket(packet:WSPacket): String {
        return WSPacket.CODEC.encodeStart(JsonOps.INSTANCE, packet).orThrow.toString()
    }

    fun close() {
        this.client.close()
        this.interrupt()
    }

    fun requestCompletion(input: String, cursorPos: Int): CompletableFuture<List<String>> {
        val packet = WSCompletionRequestPacket(input, cursorPos)
        packet(packet)
        return CompletableFuture<List<String>>().also { completionCallback[packet.requestId] = it }
    }

    fun handleCompletionResult(requestId: String, result: List<String>) {
        synchronized(completionCallback) {
            if (requestId !in completionCallback) return
            completionCallback[requestId]!!.complete(result)
        }
    }
}