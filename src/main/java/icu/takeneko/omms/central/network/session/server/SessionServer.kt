package icu.takeneko.omms.central.network.session.server

import icu.takeneko.omms.central.config.Config.config
import icu.takeneko.omms.central.controller.console.ControllerConsole
import icu.takeneko.omms.central.network.EncryptedSocket
import icu.takeneko.omms.central.network.session.FuseEncryptedSocket
import icu.takeneko.omms.central.network.session.RateExceedException
import icu.takeneko.omms.central.network.session.Session
import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.request.RequestManager.getRequestHandler
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.network.session.response.Result
import icu.takeneko.omms.central.permission.Permission
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.net.SocketException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.Executors
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class SessionServer(private val session: Session, private var permissions: List<Permission>) : Thread() {
    private lateinit var fuseEncryptedSocket: FuseEncryptedSocket
    private val logger: Logger = LoggerFactory.getLogger("SessionServer")
    private var sessionContext: SessionContext? = null
    private val executorService = Executors.newSingleThreadExecutor()

    init {
        val socket = session.socket
        setName(String.format("SessionServer#%s:%d", socket.getInetAddress().hostAddress, socket.getPort()))
        try {
            val encryptedConnector = EncryptedSocket(
                BufferedReader(
                    InputStreamReader(session.socket.getInputStream())
                ),
                PrintWriter(
                    OutputStreamWriter(session.socket.getOutputStream())
                ), String(
                    session.key
                )
            )
            fuseEncryptedSocket = FuseEncryptedSocket.of(encryptedConnector, config.rateLimit)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (socket.isClosed) {
            throw RuntimeException()
        }
    }

    private fun runOnNetworkThread(runnable: Runnable) {
        executorService.submit(runnable)
    }

    fun sendResponseAsync(response: Response?) {
        runOnNetworkThread {
            try {
                fuseEncryptedSocket.sendResponse(response!!)
            } catch (e: NoSuchPaddingException) {
                logger.error("Error while sending response.", e)
                throw RuntimeException(e)
            } catch (e: IllegalBlockSizeException) {
                logger.error("Error while sending response.", e)
                throw RuntimeException(e)
            } catch (e: NoSuchAlgorithmException) {
                logger.error("Error while sending response.", e)
                throw RuntimeException(e)
            } catch (e: BadPaddingException) {
                logger.error("Error while sending response.", e)
                throw RuntimeException(e)
            } catch (e: InvalidKeyException) {
                logger.error("Error while sending response.", e)
                throw RuntimeException(e)
            }
        }
    }

    private fun cleanUp() {
        sessionContext!!.controllerConsoleMap.forEach { (s: String?, controllerConsole: ControllerConsole) ->
            logger.info("Closing controller console $s")
            controllerConsole.close()
        }
    }

    override fun run() {
        logger.info("$name started.")
        sessionContext = SessionContext(
            this,
            fuseEncryptedSocket,
            session,
            permissions
        )
        try {
            while (true) {
                try {
                    if (session.socket.isClosed) break
                    val request = fuseEncryptedSocket.receiveRequest()
                    logger.debug("Received {}", request)
                    val handler = getRequestHandler(Objects.requireNonNull(request).request) ?: continue
                    val permission = handler.requiresPermission()
                    if (permission != null && !permissions.contains(permission)) {
                        sendResponseAsync(
                            Response()
                                .withResponseCode(Result.PERMISSION_DENIED)
                        )
                        continue
                    }
                    var response: Response?
                    try {
                        response = handler.handle(request, sessionContext)
                        if (response == null) {
                            logger.info("Session terminated.")
                            fuseEncryptedSocket.sendResponse(
                                Response()
                                    .withResponseCode(Result.DISCONNECT)
                            )
                            session.socket.close()
                            break
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        response = Response()
                            .withResponseCode(Result.FAIL).withContentPair("error", t.toString())
                    }
                    if (session.socket.isClosed) {
                        break
                    }
                    sendResponseAsync(response)
                } catch (e: NullPointerException) {
                    break
                } catch (e: SocketException) {
                    logger.warn(e.toString())
                    break
                } catch (e: RateExceedException) {
                    sendResponseAsync(
                        Response()
                            .withResponseCode(Result.RATE_LIMIT_EXCEEDED)
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
    }
}
