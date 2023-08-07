package net.zhuruoling.omms.central.network.http.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.system.info.SystemInfoUtil
import net.zhuruoling.omms.central.util.Util
import org.slf4j.LoggerFactory

fun Route.controllerStatusQueryRouting() {
    val logger = LoggerFactory.getLogger("StatusQueryRouting")
    route("/status"){
        get{
            call.respondText(contentType = ContentType.Text.Plain, status = HttpStatusCode.OK) {
                Util.toJson(SystemInfoUtil.getSystemInfo())
            }
        }
        get ("controllers"){
            call.respondText(contentType = ContentType.Text.Plain, status = HttpStatusCode.OK){
                Util.toJson(ControllerManager.getControllerStatus(
                    ControllerManager.controllers.keys.toMutableList()
                ))
            }
        }
    }
}
