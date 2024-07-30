package icu.takeneko.omms.central.network.session.server

import icu.takeneko.omms.central.controller.console.ControllerConsole
import icu.takeneko.omms.central.network.chatbridge.Broadcast
import icu.takeneko.omms.central.network.session.RateExceedException
import icu.takeneko.omms.central.network.session.Session
import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.request.RequestManager.getRequestHandler
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.network.session.response.Result
import icu.takeneko.omms.central.permission.Permission
import icu.takeneko.omms.central.util.Util
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.SocketException

class SessionServer(private val session: Session, private var permissions: List<Permission>) {
    private val sessionChannel = session.createChannel()
    private val logger: Logger = LoggerFactory.getLogger("SessionServer")
    private lateinit var sessionContext: SessionContext
    private var shouldKeepRunning = true

    init {
        val socket = session.socket
        if (socket.isClosed) {
            throw RuntimeException()
        }
    }

    private fun cleanUp() {
        sessionContext.controllerConsoleMap.forEach { (s: String?, controllerConsole: ControllerConsole) ->
            logger.info("Closing controller console $s")
            controllerConsole.close()
        }
    }

    fun sendBroadcastMessage(broadcast: Broadcast) {
        runBlocking {
            sessionChannel.sendResponse(Response(Result.BROADCAST_MESSAGE, buildMap<String, String> {
                this["broadcast"] = Util.toJson(broadcast)
            }))
        }
    }

    fun requestSessionTermination() {
        shouldKeepRunning = false
        logger.info("Requested session termination")
    }

    suspend fun handleRequest() {
        logger.info("SessionServer started.")
        sessions += this
        sessionContext = SessionContext(
            this,
            sessionChannel,
            session,
            permissions
        )
        try {
            while (shouldKeepRunning) {
                try {
                    if (session.socket.isClosed) break
                    val request = sessionChannel.receiveRequest()
                    if (request == null){
                        logger.warn("Null request received, disconnecting.")
                        sessionChannel.sendResponse(Response().withResponseCode(Result.DISCONNECT))
                        break
                    }
                    if (!shouldKeepRunning) break
                    logger.debug("Received {}", request)
                    val handler = getRequestHandler(request.request)
                    if (handler == null){
                        logger.warn("No handler registered for ${request.request}, dropping request.")
                        continue
                    }
                    val permission = handler.requiresPermission()
                    if (permission != null && !permissions.contains(permission)) {
                        sessionChannel.sendResponse(
                            Response().withResponseCode(Result.PERMISSION_DENIED)
                        )
                        continue
                    }
                    withContext(Dispatchers.IO) {
                        launch {
                            val response: Response? = try {
                                handler.handle(request, sessionContext)
                            } catch (t: Throwable) {
                                t.printStackTrace()
                                Response().withResponseCode(Result.FAIL).withContentPair("error", t.toString())
                            } ?: run {
                                logger.info("Session terminated.")
                                sessionChannel.sendResponse(
                                    Response()
                                        .withResponseCode(Result.DISCONNECT)
                                )
                                requestSessionTermination()
                                null
                            }
                            if (response != null) {
                                sessionChannel.sendResponse(response)
                            }
                        }
                    }
                    if (session.socket.isClosed) {
                        break
                    }
                } catch (e: NullPointerException) {
                    break
                } catch (e: SocketException) {
                    logger.warn(e.toString())
                    break
                } catch (e: RateExceedException) {
                    sessionChannel.sendResponse(
                        Response().withResponseCode(Result.RATE_LIMIT_EXCEEDED)
                    )
                    logger.warn("Rate limit exceeded.")
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                    break
                }
            }
            logger.info("Disconnecting.")
        } catch (e: Throwable) {
            RuntimeException(e).printStackTrace()
        }
        cleanUp()
        sessions -= this
    }

    fun sendResponseBlocking(response: Response) {
        runBlocking {
            sessionChannel.sendResponse(response)
        }
    }

    companion object {
        val sessions = mutableListOf<SessionServer>()
    }
}
