package net.zhuruoling.omms.central.controller.console

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.zhuruoling.omms.central.controller.ControllerImpl
import net.zhuruoling.omms.central.network.http.client.asSalted
import java.util.concurrent.atomic.AtomicBoolean

class ControllerWebSocketSession(
    val onLogReceiveCallback: ControllerWebSocketSession.(String) -> Unit,
    private val controllerImpl: ControllerImpl
) :
    Thread("Console@${controllerImpl.name}") {
    val list = mutableListOf<String>()
    var connected = AtomicBoolean(false)
    val client = HttpClient(CIO) {
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

    fun getHistoryLogs() {
        val baseUrl = "http://" + controllerImpl.httpQueryAddress + "/"
        runBlocking {
            val result = client.get(baseUrl + "logs")
            val content = String(result.readBytes())
            content.split("\n").forEach {
                onLogReceiveCallback(it)
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
                                    runBlocking {
                                        onLogReceiveCallback(line.readText())
                                    }
                                }
                            }
                            val input = launch(Dispatchers.Default) {
                                while (true) {
                                    synchronized(list) {
                                        if (list.isNotEmpty()) {
                                            for (s in list) {
                                                runBlocking {
                                                    this@webSocket.send(s)
                                                }
                                            }
                                            list.clear()
                                        }
                                    }
                                    sleep(50)
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

    fun inputLine(line: String) {
        synchronized(list) {
            list.add(line)
        }
    }

    fun close() {
        this.client.close()
        this.interrupt()
    }
}