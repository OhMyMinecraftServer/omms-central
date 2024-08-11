package icu.takeneko.omms.central.controller.console.ws

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import icu.takeneko.omms.central.controller.ControllerImpl
import icu.takeneko.omms.central.controller.asSalted
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.LockSupport

class ControllerWebSocketSession(
    private val controllerImpl: ControllerImpl,
    private val handler: WSPacketHandler
) : Thread("Console@${controllerImpl.name}") {
    private val codec: Codec<Either<WSStatusPacket, WSStringPacket>> = Codec.either(
        WSStatusPacket.CODEC,
        WSStringPacket.CODEC
    )
    private val gson = Gson()
    private val logger = LoggerFactory.getLogger("ControllerWSConsoleImpl")
    val list = mutableListOf<WSPacket>()
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
                    BasicAuthCredentials(username = controllerImpl.name, password = asSalted(controllerImpl.name))
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
                                            val jElem = JsonParser.parseString(s)
                                            codec.decode(JsonOps.INSTANCE, jElem)
                                                .getOrThrow(false, logger::error)
                                                .first
                                                .map(WSPacket::cast, WSPacket::cast)
                                                .handle(handler)
                                        } catch (e: Exception) {
                                            logger.error("Message parse failed:", e)
                                        }
                                    }
                                }
                            }
                            val input = launch(Dispatchers.IO) {
                                while (true) {
                                    synchronized(list) {
                                        if (list.isNotEmpty()) {
                                            for (s in list) {
                                                runBlocking {
                                                    val e = codec.encodeStart(
                                                        JsonOps.INSTANCE, when (s) {
                                                            is WSStatusPacket -> Either.left(s)
                                                            is WSStringPacket -> Either.right(s)
                                                            else -> return@runBlocking
                                                        }
                                                    ).getOrThrow(false, logger::error).toString()
                                                    this@webSocket.send(e)
                                                }
                                            }
                                            list.clear()
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
        synchronized(list) {
            list.add(packet)
        }
    }

    fun command(line: String) {
        synchronized(list) {
            list.add(WSStringPacket(PacketType.COMMAND, line))
        }
    }

    fun close() {
        this.client.close()
        this.interrupt()
    }
}