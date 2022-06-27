package net.zhuruoling.server.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import net.zhuruoling.server.routes.whitelistQueryRouting

fun Application.configureRouting(){
    routing {
        whitelistQueryRouting()
    }
}