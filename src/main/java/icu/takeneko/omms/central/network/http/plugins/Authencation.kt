package icu.takeneko.omms.central.network.http.plugins

import icu.takeneko.omms.central.controller.ControllerManager
import icu.takeneko.omms.central.security.HttpAuthUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun Application.configureAuthentication() {
    authentication {
        form(name = "omms-simple-auth") {
            challenge {
                this.call.respond(HttpStatusCode.Unauthorized)
            }
            validate {
                return@validate if (HttpAuthUtil.checkTokenMatches(it)) UserIdPrincipal(it.name + it.password) else null
            }
        }
        basic(name = "omms-auth") {
            realm = "omms simple auth"
            validate {
                return@validate if (HttpAuthUtil.checkTokenMatches(it)) UserIdPrincipal(it.name + it.password) else null
            }
        }
        basic(name = "omms-controller-auth") {
            realm = "chatbridge"
            validate {
                return@validate if (ControllerManager.controllers.containsKey(it.name)) UserIdPrincipal(it.name + it.password) else null
            }
        }
    }

}