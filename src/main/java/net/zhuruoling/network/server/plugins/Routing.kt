package net.zhuruoling.network.server.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import net.zhuruoling.network.server.routes.whitelistQueryRouting

fun Application.configureRouting(){
    routing {
        whitelistQueryRouting()
    }
}