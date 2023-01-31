package net.zhuruoling.omms.central.network.http.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import net.zhuruoling.omms.central.console.CommandSourceStack
import net.zhuruoling.omms.central.console.ConsoleCommandHandler
import net.zhuruoling.omms.central.main.RuntimeConstants.publicLogger
import net.zhuruoling.omms.central.util.Util

@OptIn(DelicateCoroutinesApi::class)
fun Route.commandUpstreamRouting(){
    route("/command"){

    }
}