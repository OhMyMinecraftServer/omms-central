package net.zhuruoling.network.http

import io.ktor.server.application.*
import io.ktor.server.netty.*
import net.zhuruoling.main.RuntimeConstants
import net.zhuruoling.network.http.plugins.configureAuthencation
import net.zhuruoling.network.http.plugins.configureRouting
import net.zhuruoling.network.http.plugins.configureSerialization

fun httpServerMain(args: Array<String>) = EngineMain.main(args)

fun launchHttpServerAsync(args: Array<String>): Thread {
    val arguments = args + "-port=${RuntimeConstants.config?.httpPort}"
    val thread = Thread {
        try {
            httpServerMain(arguments)
        } catch (ignored: InterruptedException) { }
    }
    thread.name = "HttpServer"
    thread.start()
    return thread
}



fun Application.module() {
    configureRouting()
    configureSerialization()
    configureAuthencation()
}