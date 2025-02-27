package icu.takeneko.omms.central.network.http.routes

import icu.takeneko.omms.central.network.http.Status
import icu.takeneko.omms.central.util.Util
import icu.takeneko.omms.central.whitelist.WhitelistManager
import icu.takeneko.omms.central.whitelist.WhitelistNotExistException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

data class HttpResponse(val status: Status, val data: MutableList<String>)

fun Route.whitelistQueryRouting() {
    val logger = LoggerFactory.getLogger("WhitelistQueryRouting")
    route("/whitelist") {
        get {
            logger.debug("Querying whitelist names.")
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
            logger.debug("Querying whitelist $name content.")
            val content = WhitelistManager.getWhitelist(name)
            if (content == null) {
                call.respondText(
                    Util.toJson(HttpResponse(status = Status.WHITELIST_NOT_EXIST, mutableListOf())),
                    status = HttpStatusCode.OK
                )
                return@get
            }
            call.respondText(
                Util.toJson(HttpResponse(status = Status.OK, content.players.toMutableList())),
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
            try {
                val result = WhitelistManager.queryWhitelist(name, playerName)
                call.respondText(status = HttpStatusCode.OK) {
                    "{\"result\":\"${if (result) Status.OK else Status.PLAYER_NOT_EXIST}\"}"
                }
            } catch (e: WhitelistNotExistException) {
                call.respondText(status = HttpStatusCode.OK) {
                    "{\"result\":\"${Status.WHITELIST_NOT_EXIST}\"}"
                }
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
                call.respond(listOf<String>())
            }
            val result = WhitelistManager.queryInAllWhitelist(playerName)
            call.respond(
                status = HttpStatusCode.OK, if (result.isEmpty()) {
                    logger.warn("Querying player $playerName in all whitelists, but this player was not found in any whitelist.")
                    listOf()
                } else {
                    result
                }
            )
        }
    }
}