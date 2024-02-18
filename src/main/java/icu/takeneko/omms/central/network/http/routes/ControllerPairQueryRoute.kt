package icu.takeneko.omms.central.network.http.routes

import icu.takeneko.omms.central.network.pair.PairManager
import icu.takeneko.omms.central.util.Util
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Route.controllerPairQueryRoute() {
    val logger = LoggerFactory.getLogger("ControllerPairQueryRoute")
    route("/pair") {
        get("{code?}") {
            val code = call.parameters["code"] ?: return@get call.respondText(
                "Missing code",
                status = HttpStatusCode.BadRequest
            )
            try {
                if (PairManager.enabled) {
                    val result = object {
                        val result = "OK"
                        val config = PairManager[code]
                    }

                    call.respondText(
                        Util.toJson(result),
                        status = HttpStatusCode.OK
                    )
                } else {
                    return@get call.respondText(
                        "{\"result\":\"Pair is not enabled on this server\",\"config\":{}}",
                        status = HttpStatusCode.BadRequest
                    )
                }
            } catch (ignored: IllegalArgumentException) {
                call.respondText(
                    "Code NOT exist",
                    status = HttpStatusCode.NotFound
                )
            }

        }
    }
}