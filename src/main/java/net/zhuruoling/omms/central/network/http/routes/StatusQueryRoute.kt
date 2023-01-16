package net.zhuruoling.omms.central.network.http.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.controller.Status
import net.zhuruoling.omms.central.system.SystemUtil
import net.zhuruoling.omms.central.util.Util
import org.slf4j.LoggerFactory

fun Route.statusQueryRouting() {
    val logger = LoggerFactory.getLogger("StatusQueryRouting")
    route("/status"){
        get{
            this.context.respondText(Util.toJson(SystemUtil.getSystemInfo()))
        }
        get ("controllers"){
            this.context.respondText(Util.toJson(ControllerManager.getControllerStatus(
                ControllerManager.controllers.keys.toMutableList()
            )))
        }
        post("/upload") {
            val statusString = call.receiveText()
            val status = Util.fromJson(statusString, Status::class.java)
            ControllerManager.putStatusCache(status)
        }
    }
}
