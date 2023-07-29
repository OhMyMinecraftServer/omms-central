package net.zhuruoling.omms.central.network.http.plugins

import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
//        json(contentType = ContentType.Application.Json)
        gson(contentType = ContentType.Application.Json) {
            this.serializeNulls()
        }
    }
}