package net.zhuruoling.omms.central.network.http.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.omms.central.network.http.routes.*
import net.zhuruoling.omms.central.util.Util

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
        controllerQueryRouting()
        crashReportQueryRoute()
        authenticate("omms-auth") {
            commandUpstreamRouting()
            controllerStatusQueryRouting()
            managementQueryRouting()
        }
    }
}