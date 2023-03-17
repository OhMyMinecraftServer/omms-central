package net.zhuruoling.omms.central.network.http.routes

import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.websocketRoute(){
    route("/"){
        webSocket {

        }
    }
}