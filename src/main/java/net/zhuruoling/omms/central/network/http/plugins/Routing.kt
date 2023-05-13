package net.zhuruoling.omms.central.network.http.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.network.ChatbridgeImplementation
import net.zhuruoling.omms.central.network.http.routes.*
import net.zhuruoling.omms.central.plugin.callback.HttpServerLoadCallback
import net.zhuruoling.omms.central.util.Util

fun Application.configureRouting() {
    routing {
        get {
            call.respondText(status = HttpStatusCode.OK) {
                Util.PRODUCT_NAME
            }
        }
        whitelistQueryRouting()
        announcementQueryRouting()
        controllerPairQueryRoute()
        crashReportQueryRoute()
        authenticate("omms-auth") {
            commandUpstreamRouting()
            controllerStatusQueryRouting()
            managementQueryRouting()
        }
        if (GlobalVariable.config!!.chatbridgeImplementation == ChatbridgeImplementation.WS) {
            authenticate("omms-controller-auth") {
                websocketRoute()
            }
        }
        HttpServerLoadCallback.INSTANCE.invokeAll(this@configureRouting)
    }
}