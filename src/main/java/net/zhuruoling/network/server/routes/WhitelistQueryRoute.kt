package net.zhuruoling.network.server.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.zhuruoling.whitelist.WhitelistManager
import net.zhuruoling.whitelist.WhitelistReader
import net.zhuruoling.whitelist.WhitelistResult
import org.slf4j.LoggerFactory

fun Route.whitelistQueryRouting() {
    val logger = LoggerFactory.getLogger("WhitelistQueryRouting")
    route("/whitelist") {
        get {
            logger.info("Querying whitelist names.")
            val whitelists = WhitelistReader().whitelists
            if (whitelists == null) {
                call.respondText("No Whitelists found.", status = HttpStatusCode.OK)
            }
            val whitelistNames = arrayOfNulls<String>(whitelists!!.size)
            for (i in whitelistNames.indices) {
                whitelistNames[i] = whitelists[i].name
            }
            call.respond(whitelistNames)
        }
        get("{name?}") {
            val name = call.parameters["name"] ?: return@get call.respondText(
                "Missing name",
                status = HttpStatusCode.BadRequest
            )

            logger.info("Querying whitelist $name content.")
            val content = WhitelistReader().read(name)
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
            if (result == WhitelistResult.OK) {
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
            val whitelists = WhitelistReader().whitelists
            if (whitelists == null) {
                call.respondText("No Whitelists found.", status = HttpStatusCode.OK)
            }
            val whitelistNames = arrayOfNulls<String>(whitelists!!.size)
            for (i in whitelistNames.indices) {
                whitelistNames[i] = whitelists[i].name
            }
            val succeed = mutableListOf<String>()
            whitelistNames.forEach {
                if (WhitelistManager.queryWhitelist(it, playerName) == WhitelistResult.OK) {
                    if (it != null) {
                        succeed.add(it)
                    }
                }
            }
            if (succeed.isEmpty()) {
                call.respondText("", status = HttpStatusCode.NotFound)
            } else {
                call.respond(succeed)
            }
        }
    }
}