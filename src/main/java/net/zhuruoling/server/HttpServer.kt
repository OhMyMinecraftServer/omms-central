package net.zhuruoling.server

import io.ktor.server.application.*
import net.zhuruoling.server.plugins.configureRouting
import net.zhuruoling.server.plugins.configureSerialization

fun launchHttpServerAsync(args: Array<String>) {
   val thread = Thread(Runnable {
        httpServerMain(args)
    })
    thread.name = "HttpServer"
    thread.start()
}

fun httpServerMain(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)


fun Application.module(){
    configureRouting()
    configureSerialization()
}