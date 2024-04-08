package icu.takeneko.omms.central.network.http.plugins

import icu.takeneko.omms.central.config.Config.config
import icu.takeneko.omms.central.network.ChatbridgeImplementation
import icu.takeneko.omms.central.network.http.routes.*
import icu.takeneko.omms.central.plugin.callback.HttpServerLoadCallback
import icu.takeneko.omms.central.util.versionInfoString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get {
            call.respondText(status = HttpStatusCode.OK) {
                versionInfoString
            }
        }
        authenticate("omms-api-auth") {
            httpApiQueryRouting()
        }
        authenticate("omms-controller-auth") {
            announcementQueryRouting()
            whitelistQueryRouting()
            identityQueryRoute()
            crashReportQueryRoute()
        }
        if (config.chatbridgeImplementation == ChatbridgeImplementation.WS) {
            authenticate("omms-controller-auth") {
                websocketRoute()
            }
        }
        HttpServerLoadCallback.INSTANCE.invokeAll(this@configureRouting)
    }
}