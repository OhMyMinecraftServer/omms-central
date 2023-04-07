package net.zhuruoling.omms.central.network.http.routes

import net.zhuruoling.omms.central.network.session.response.Result
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.omms.central.util.Util
import net.zhuruoling.omms.central.whitelist.WhitelistManager
import org.slf4j.LoggerFactory

data class HttpResponse(val result: Result, val data: MutableList<String>)

fun Route.whitelistQueryRouting() {
    val logger = LoggerFactory.getLogger("WhitelistQueryRouting")
    route("/whitelist") {
        get {
            logger.info("Querying whitelist names.")
            val whitelistNames = WhitelistManager.getWhitelistNames()
            if (whitelistNames.isEmpty()) {
                call.respondText("[]", status = HttpStatusCode.OK)
            }
            call.respondText(status = HttpStatusCode.OK) {
                Util.toJson(whitelistNames)
            }
        }
        get("{name?}") {
            val name = call.parameters["name"] ?: return@get call.respondText(
                "Missing name",
                status = HttpStatusCode.BadRequest
            )
            logger.info("Querying whitelist $name content.")
            val content = WhitelistManager.getWhitelist(name)
            if (content == null) {
                call.respondText(
                    Util.toJson(HttpResponse(result = Result.WHITELIST_NOT_EXIST, mutableListOf())),
                    status = HttpStatusCode.OK
                )
                return@get
            }
            call.respondText(
                Util.toJson(HttpResponse(result = Result.OK, content.players.toMutableList())),
                status = HttpStatusCode.OK
            )
        }
        get("{name?}/query/{playerName?}") {
            val name = call.parameters["name"] ?: return@get call.respondText(
                "Missing name",
                status = HttpStatusCode.BadRequest
            )
            val playerName = call.parameters["playerName"] ?: return@get call.respondText(
                "Missing player",
                status = HttpStatusCode.BadRequest
            )
            logger.debug("Querying player $playerName in $name.")
            val result = WhitelistManager.queryWhitelist(name, playerName)
            call.respondText(status = HttpStatusCode.OK) {
                "{\"result\":\"$result\"}"
            }
        }
        get("queryAll/{playerName?}") {
            val playerName = call.parameters["playerName"] ?: return@get call.respondText(
                "Missing player",
                status = HttpStatusCode.BadRequest
            )
            logger.debug("Querying player $playerName in all whitelists.")
            if (WhitelistManager.isNoWhitelist()) {
                logger.warn("Querying player $playerName in all whitelists, but no whitelist was found.")
                call.respondText("[]", status = HttpStatusCode.OK)
            }
            val result = WhitelistManager.queryInAllWhitelist(playerName)
            call.respondText(status = HttpStatusCode.OK) {
                if (result.isEmpty()) {
                    logger.warn("Querying player $playerName in all whitelists, but this player was not found in any whitelist.")
                    "[]"
                } else {
                    Util.toJson(result)
                }
            }
        }
    }
}