package net.zhuruoling.network.http.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.system.SystemInfo
import net.zhuruoling.system.SystemUtil
import net.zhuruoling.util.Util
import org.slf4j.LoggerFactory

fun Route.statusQueryRouting() {
    val logger = LoggerFactory.getLogger("StatusQueryRouting")
    route("/status"){
        get{
            this.context.respondText(Util.toJson(SystemUtil.getSystemInfo()))
        }
    }
}
