package net.zhuruoling.network.http.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.network.http.routes.*
import net.zhuruoling.util.Util

fun Application.configureRouting() {
    routing {
        get {
            call.respondText(status = HttpStatusCode.OK){
                Util.PRODUCT_NAME
            }
        }
        whitelistQueryRouting()
        announcementQueryRouting()
        controllerPairQueryRoute()
        authenticate("omms-auth") {
            commandUpstreamRouting()
            statusQueryRouting()
            managementQueryRouting()
        }

    }
}