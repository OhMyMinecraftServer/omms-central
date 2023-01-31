package net.zhuruoling.omms.central.network.http.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import net.zhuruoling.omms.central.controller.Controller

class ControllerHttpClient {
    val controller: Controller
    val client: HttpClient
    private val baseUrl: String

    constructor(controller: Controller) {
        this.controller = controller
        client = HttpClient(CIO) {
            engine {
                threadsCount = 4
                pipelining = true
            }
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(username = "jetbrains", password = "foobar")
                    }
                    realm = "Access to the '/' path"
                }
            }
        }
        baseUrl = "http://" + controller.httpQueryAddress
    }

    private suspend fun get(path: String): HttpResponse {
        return client.get(makeUrl(path))
    }

    private suspend fun post(path: String): HttpResponse {
        return client.post(makeUrl(path)) {

        }
    }

    private fun makeUrl(path: String) = baseUrl + path
}
