package net.zhuruoling.omms.central.network.http.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import net.zhuruoling.omms.central.controller.CommandOutputData
import net.zhuruoling.omms.central.controller.ControllerImpl
import net.zhuruoling.omms.central.controller.Status
import net.zhuruoling.omms.central.util.Util
import org.slf4j.LoggerFactory

fun asSalted(original: String) = original.encodeBase64() + "WTF IS IT".encodeBase64()

class ControllerHttpClient(val controllerImpl: ControllerImpl) {
    val client: HttpClient
    private val baseUrl: String
    private val logger: org.slf4j.Logger

    init {
        client = HttpClient(CIO) {
            engine {
                threadsCount = 4
                pipelining = true
            }
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(username = controllerImpl.name, password = asSalted(controllerImpl.name))
                    }
                    realm = "Access to the client"
                }
            }
        }
        logger = LoggerFactory.getLogger("ControllerHttpClient#${controllerImpl.name}")
        baseUrl = "http://" + controllerImpl.httpQueryAddress + "/"
    }

    private suspend fun get(path: String): HttpResponse {
        return client.get(makeUrl(path))
    }

    private suspend fun post(path: String, content: String): HttpResponse {
        return client.post(makeUrl(path)) {
            headers {
                append(HttpHeaders.ContentType, "text/plain")
                append(HttpHeaders.UserAgent, "OMMS Central Http Client")
            }
            setBody(content)
        }
    }

    private fun makeUrl(path: String) = baseUrl + path

    fun sendCommand(command: String): MutableList<String> {
        val result = mutableListOf<String>()
        runBlocking {
            try {
                val response = post("runCommand", command)
                val text = String(response.readBytes())
                val data = when (response.status) {
                    HttpStatusCode.OK -> {
                        Util.fromJson(text, CommandOutputData::class.javaObjectType)
                    }

                    HttpStatusCode.Unauthorized -> {
                        throw RequestUnauthorisedException("ControllerName", controllerImpl.name)
                    }

                    else -> {
                        null
                    }
                }
                if (data != null) {
                    result.addAll(data.output.split("\n"))
                }
            } catch (e: Exception) {
                if (e is RequestUnauthorisedException) {
                    throw e
                }
                logger.warn(e.toString())
            }
        }
        return result
    }

    fun queryStatus(): Status {
        var status = Status()
        var ex: Exception? = null
        status.isAlive = false
        status.isQueryable = controllerImpl.isStatusQueryable
        runBlocking {
            try {
                val response = get("status")
                val text = String(response.readBytes())
                val result = when (response.status) {
                    HttpStatusCode.OK -> {
                        Util.fromJson(text, Status::class.javaObjectType).run {
                            isAlive = true
                            isQueryable = true
                            this
                        }
                    }

                    HttpStatusCode.Unauthorized -> {
                        throw RequestUnauthorisedException("ControllerName", controllerImpl.name)
                    }

                    else -> {
                        Status()
                    }
                }
                status = result
            } catch (e: Exception) {
                ex = e
            }
        }
        if (ex != null) {
            throw ex!!
        }
        return status
    }
}

class RequestUnauthorisedException(val key: String, controllerName: String) :
    java.lang.IllegalArgumentException("Request to controller $controllerName was refused.")
