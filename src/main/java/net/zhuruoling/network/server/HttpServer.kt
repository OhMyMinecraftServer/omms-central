package net.zhuruoling.network.server

import io.ktor.server.application.*
import net.zhuruoling.network.server.plugins.configureRouting
import net.zhuruoling.network.server.plugins.configureSerialization

fun launchHttpServerAsync(args: Array<String>): Thread {
    val thread = Thread {
        httpServerMain(args)
    }
    thread.name = "HttpServer"
    thread.start()
    return thread
}

fun httpServerMain(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)


fun Application.module() {
    configureRouting()
    configureSerialization()
}