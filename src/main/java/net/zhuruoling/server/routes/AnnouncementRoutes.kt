package net.zhuruoling.server.routes

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.announcementRouting() {
    route("/announcement") {
        get {
            call.respondText(Gson().toJson(object {
                val response = "pong"
            }))
        }
        get("{id?}") {

        }
        post {

        }
        delete("{id?}") {

        }
    }
}