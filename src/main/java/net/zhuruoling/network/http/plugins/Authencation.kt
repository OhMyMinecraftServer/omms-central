package net.zhuruoling.network.http.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import net.zhuruoling.security.HttpAuthUtil

fun Application.configureAuthencation() {
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
    }

}