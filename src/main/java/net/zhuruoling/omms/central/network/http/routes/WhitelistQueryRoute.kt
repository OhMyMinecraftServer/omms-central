package net.zhuruoling.omms.central.network.http.routes

import net.zhuruoling.omms.central.network.session.response.Result
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.omms.central.whitelist.WhitelistManager
import org.slf4j.LoggerFactory

fun Route.whitelistQueryRouting() {
    val logger = LoggerFactory.getLogger("WhitelistQueryRouting")
    route("/whitelist") {
        get {
            logger.info("Querying whitelist names.")
            val whitelistNames = WhitelistManager.getWhitelistNames()
            if (whitelistNames.isEmpty()) {
                call.respondText("No Whitelists found.", status = HttpStatusCode.OK)
            }
            call.respond(whitelistNames)
        }
        get("{name?}") {
            val name = call.parameters["name"] ?: return@get call.respondText(
                "Missing name",
                status = HttpStatusCode.BadRequest
            )

            logger.info("Querying whitelist $name content.")
            val content = WhitelistManager.getWhitelist(name)
            if (content == null) {
                call.respondText("", status = HttpStatusCode.NotFound)
                return@get
            }
            call.respond(content.players)
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
            logger.info("Querying player $playerName in $name.")
            val result = WhitelistManager.queryWhitelist(name, playerName)
            if (result == Result.OK) {
                call.respondText("", status = HttpStatusCode.OK)
            } else {
                call.respondText("", status = HttpStatusCode.NotFound)
            }
        }
        get("queryAll/{playerName?}") {
            val playerName = call.parameters["playerName"] ?: return@get call.respondText(
                "Missing player",
                status = HttpStatusCode.BadRequest
            )
            logger.info("Querying player $playerName in all whitelists.")
            if (WhitelistManager.isNoWhitelist()) {
                call.respondText("No Whitelists found.", status = HttpStatusCode.OK)
            }
            val result = WhitelistManager.queryInAllWhitelist(playerName)
            if (result.isEmpty()) {
                call.respondText("", status = HttpStatusCode.NotFound)
            } else {
                call.respond(result)
            }
        }
    }
}