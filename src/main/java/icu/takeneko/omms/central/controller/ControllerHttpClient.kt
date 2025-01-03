package icu.takeneko.omms.central.controller

import icu.takeneko.omms.central.util.Util
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

class ControllerHttpClient(private val controllerImpl: ControllerImpl) {
    private val client: HttpClient = HttpClient(CIO) {
        engine {
            threadsCount = 4
            pipelining = true
        }
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username = controllerImpl.name, password = controllerImpl.name.encodeBase64())
                }
                realm = "Access to the client"
            }
        }
    }
    private val baseUrl: String = "http://" + controllerImpl.httpQueryAddress + "/"
    private val logger: org.slf4j.Logger = LoggerFactory.getLogger("ControllerHttpClient#${controllerImpl.name}")

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

    fun queryControllerMapping() {
        runBlocking {
            try {
                val response = get("controllerMapping")
                val content = String(response.readBytes())

            } catch (e: Exception) {

            }
        }
    }

    fun sendCommand(command: String): CompletableFuture<CommandExecutionResult> {
        val result = CompletableFuture<CommandExecutionResult>()
        runBlocking {
            try {
                val response = post("runCommand", command)
                val text = String(response.readBytes())
                when (response.status) {
                    HttpStatusCode.OK -> {
                        result.complete(Util.fromJson(text, CommandExecutionResult::class.javaObjectType))
                    }

                    HttpStatusCode.Unauthorized -> {
                        result.completeExceptionally(
                            RequestUnauthorisedException(
                                controllerImpl.name
                            )
                        )
                    }

                    else -> {
                        result.completeExceptionally(RuntimeException("Unexpected result: ${response.status}"))
                    }
                }

            } catch (e: Exception) {
                result.completeExceptionally(e)
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
                        throw RequestUnauthorisedException(controllerImpl.name)
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

class RequestUnauthorisedException(val controllerName: String) :
    java.lang.IllegalArgumentException("Request to controller $controllerName was refused.")
