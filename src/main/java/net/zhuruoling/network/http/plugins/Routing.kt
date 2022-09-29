package net.zhuruoling.network.http.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import net.zhuruoling.network.http.routes.announcementQueryRouting
import net.zhuruoling.network.http.routes.whitelistQueryRouting

fun Application.configureRouting() {
    routing {
        whitelistQueryRouting()
        announcementQueryRouting()
    }
}