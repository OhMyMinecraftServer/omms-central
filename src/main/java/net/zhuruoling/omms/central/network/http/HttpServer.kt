package net.zhuruoling.omms.central.network.http

import io.ktor.server.application.*
import io.ktor.server.netty.*
import net.zhuruoling.omms.central.main.RuntimeConstants
import net.zhuruoling.omms.central.network.http.plugins.configureAuthentication
import net.zhuruoling.omms.central.network.http.plugins.configureRouting
import net.zhuruoling.omms.central.network.http.plugins.configureSerialization

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
    configureAuthentication()
}