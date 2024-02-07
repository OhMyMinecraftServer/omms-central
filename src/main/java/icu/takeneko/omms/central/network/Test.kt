package icu.takeneko.omms.central.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import icu.takeneko.omms.central.config.Config.config
import icu.takeneko.omms.central.security.HttpAuthUtil


@DelicateCoroutinesApi
fun testAuth(name: String, command: String){
    val client = HttpClient(CIO){
        install(Auth){
            basic {
                credentials {
                    BasicAuthCredentials(name, HttpAuthUtil.calculateToken(name))
                }
                realm = "omms simple auth"
            }
        }
    }
    GlobalScope.launch(Dispatchers.IO) {
        val response = client.post("http://localhost:${config.httpPort}/command/run") {
            headers.append(HttpHeaders.UserAgent, "omms controller")
            setBody(command)
        }

        println(response.status.toString())
    }
}