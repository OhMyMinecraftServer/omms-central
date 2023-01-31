package net.zhuruoling.omms.central.network.http.plugins

import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
        gson {
            this.serializeNulls()
        }
    }
}