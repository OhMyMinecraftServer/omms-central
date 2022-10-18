package net.zhuruoling.network.http.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.announcement.AnnouncementManager
import net.zhuruoling.util.Util
import org.slf4j.LoggerFactory
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

fun Route.announcementQueryRouting() {
    val logger = LoggerFactory.getLogger("WhitelistQueryRouting")
    route("/announcement") {
        get("latest") {
            val announcement =
                AnnouncementManager.getLatest() ?: return@get call.respond(HttpStatusCode.OK, "NO_ANNOUNCEMENT")
            call.respond(HttpStatusCode.OK, Util.base64Encode(announcement.toJson()))
        }
        get("get/{id?}") {
            val name = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val announcement = AnnouncementManager.get(name) ?: return@get call.respond(HttpStatusCode.OK, "NO_ANNOUNCEMENT")
            call.respond(HttpStatusCode.OK, Util.base64Encode(announcement.toJson()))
        }
        get("list") {
            call.respond(HttpStatusCode.OK, AnnouncementManager.announcementMap.keys)
        }
        get {
            val map = mutableMapOf<String, String>()
            AnnouncementManager.announcementMap.forEach {
                map[it.key] = it.value.toJson()
            }
            call.respond(HttpStatusCode.OK, map)
        }
    }
}
