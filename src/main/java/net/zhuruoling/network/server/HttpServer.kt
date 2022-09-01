package net.zhuruoling.network.server

import io.ktor.server.application.*
import io.ktor.server.netty.*
import net.zhuruoling.main.RuntimeConstants
import net.zhuruoling.network.server.plugins.configureRouting
import net.zhuruoling.network.server.plugins.configureSerialization

fun launchHttpServerAsync(args: Array<String>): Thread {
    val arguments = args + "-port=${RuntimeConstants.config?.httpPort}"
    val thread = Thread {
        httpServerMain(arguments)
    }
    thread.name = "HttpServer"
    thread.start()
    return thread
}

fun httpServerMain(args: Array<String>) = EngineMain.main(args)


fun Application.module() {
    configureRouting()
    configureSerialization()
}