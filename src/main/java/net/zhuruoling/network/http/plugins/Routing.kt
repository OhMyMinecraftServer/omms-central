package net.zhuruoling.network.http.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import net.zhuruoling.network.http.routes.*

fun Application.configureRouting() {
    routing {
        whitelistQueryRouting()
        announcementQueryRouting()
        authenticate("omms-auth") {
            commandUpstreamRouting()
            statusQueryRouting()
            managementQueryRouting()
        }

    }
}