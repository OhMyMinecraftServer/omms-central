package icu.takeneko.omms.central.network.http.routes

import icu.takeneko.omms.central.controller.ControllerManager
import icu.takeneko.omms.central.controller.crashreport.ControllerCrashReportManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

fun Route.crashReportQueryRoute() {
    val logger = LoggerFactory.getLogger("CrashReportUpload")
    route("/controller") {
        post("crashReport/upload") {
            val controller = this.context.request.headers["Controller-ID"]
                ?: return@post call.respondText(status = HttpStatusCode.BadRequest) {
                    "Require Controller-ID Header."
                }
            val content = call.receiveText()
            if (controller in ControllerManager.controllers) {
                logger.info("Got crash report from controller $controller")
                launch {
                    ControllerCrashReportManager.createNewCrashReport(controller, content)
                }
                return@post call.respondText(status = HttpStatusCode.OK) { "" }
            } else {
                return@post call.respondText(status = HttpStatusCode.BadRequest) {
                    "Controller $controller does not exist on this central server."
                }
            }
        }
    }
}
