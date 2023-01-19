package net.zhuruoling.omms.central.network.http.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.omms.central.controller.CommandOutputData
import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.controller.Status
import net.zhuruoling.omms.central.util.Util
import org.slf4j.LoggerFactory

fun Route.controllerQueryRouting() {
    val logger = LoggerFactory.getLogger("StatusQueryRouting")
    route("/controller") {
        post("status/upload") {
            val statusString = call.receiveText()
            val status = Util.fromJson(statusString, Status::class.java)
            status.isAlive = true
            status.isQueryable = true
            logger.info("Got status from ${status.name}")
            ControllerManager.putStatusCache(status)
            call.respondText(status = HttpStatusCode.OK){"GOOD ${status.name}"}
        }
        post("command/upload") {
            val commandString = call.receiveText()
            val commandOutputData = Util.fromJson(commandString, CommandOutputData::class.java)
            ControllerManager.putCommandCache(commandOutputData)
            call.respondText(status = HttpStatusCode.OK){"GOOD ${commandOutputData.controllerId}: ${commandOutputData.command}"}
        }
    }
}