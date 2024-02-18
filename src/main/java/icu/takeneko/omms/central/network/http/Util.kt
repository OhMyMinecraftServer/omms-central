package icu.takeneko.omms.central.network.http

import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.request.buildFromJson
import io.ktor.server.application.*
import io.ktor.server.request.*

suspend fun receiveTextFromCall(call: ApplicationCall): String {
    return call.receiveText()
}

suspend fun receiveRequestFromCall(call: ApplicationCall): Request? {
    return buildFromJson(receiveTextFromCall(call))
}

fun <T> joinToString(list: List<T>): String {
    return list.joinToString(separator = "\n")
}

fun <T> joinToString(list: List<T>, separator: String): String {
    return list.joinToString(separator)
}
