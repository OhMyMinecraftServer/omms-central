package net.zhuruoling.omms.central.network.http

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import net.zhuruoling.omms.central.config.Config.config
import net.zhuruoling.omms.central.network.ChatbridgeImplementation
import net.zhuruoling.omms.central.network.http.plugins.configureAuthentication
import net.zhuruoling.omms.central.network.http.plugins.configureRouting
import net.zhuruoling.omms.central.network.http.plugins.configureSerialization
import java.time.Duration

fun httpServerMain(args: Array<String>) = EngineMain.main(args)

fun launchHttpServerAsync(args: Array<String>): Thread {
    val arguments = args + "-port=${config.httpPort}"
    val thread = Thread {
        try {
            httpServerMain(arguments)
        } catch (ignored: InterruptedException) {
        }
    }
    thread.name = "HttpServer"
    thread.start()
    return thread
}

@Suppress("unused")
fun Application.module() {
    if (config.chatbridgeImplementation == ChatbridgeImplementation.WS) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(1)
            timeout = Duration.ofSeconds(3)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }
    }
    configureRouting()
    configureSerialization()
    configureAuthentication()
}