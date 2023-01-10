package net.zhuruoling.omms.central.network.http

import io.ktor.server.application.*
import io.ktor.server.request.*
import net.zhuruoling.omms.central.network.session.request.Request
import net.zhuruoling.omms.central.network.session.request.buildFromJson

suspend fun receiveTextFromCall(call:ApplicationCall): String {
    return call.receiveText()
}

suspend fun receiveRequestFromCall(call: ApplicationCall): Request? {
    return buildFromJson(receiveTextFromCall(call))
}