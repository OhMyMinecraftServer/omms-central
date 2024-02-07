package icu.takeneko.omms.central.network.http.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import icu.takeneko.omms.central.identity.IdentityProvider
import icu.takeneko.omms.central.identity.SystemIdentifier
import icu.takeneko.omms.central.util.Util

fun Route.identityQueryRoute(){
    route("/identity") {
        post("generate") {
            val s = call.receiveText()
            val systemIdentifier = Util.fromJson(s, SystemIdentifier::class.java)
            return@post call.respondText {
                IdentityProvider.generateIdentityCode(identifier = systemIdentifier)
            }
        }
    }
}