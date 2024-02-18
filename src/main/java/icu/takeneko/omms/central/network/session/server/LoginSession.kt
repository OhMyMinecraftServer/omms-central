package icu.takeneko.omms.central.network.session.server

import com.google.gson.GsonBuilder
import icu.takeneko.omms.central.config.Config.config
import icu.takeneko.omms.central.network.EncryptedSocket
import icu.takeneko.omms.central.network.session.Session
import icu.takeneko.omms.central.network.session.request.LoginRequest
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.network.session.response.Result
import icu.takeneko.omms.central.util.Util
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class LoginSession(socket: Socket) :
    Thread("LoginSession@${socket.inetAddress}:${socket.port}") {
    private val encryptedConnector: EncryptedSocket
    private val logger = LoggerFactory.getLogger("InitSession")
    private val gson = GsonBuilder().serializeNulls().create()
    private val socket: Socket

    init {

        val inputReader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
        val out = PrintWriter(socket.getOutputStream(), true)
        val date = LocalDateTime.now()
        var key = date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))
        key = Util.base64Encode(Util.base64Encode(key!!))
        logger.debug("key: $key")
        encryptedConnector = EncryptedSocket(inputReader, out, key)
        logger.info("Client: " + socket.getInetAddress() + ":" + socket.getPort() + " connected.")
        this.socket = socket
    }

    override fun run() {
        try {
            var line = encryptedConnector.readLine()
            while (true) {
                val request = gson.fromJson(line, LoginRequest::class.java)
                logger.debug("Got request:{}", request)
                if (Objects.requireNonNull(request).request == "PING") {
                    if (request.version != Util.PROTOCOL_VERSION) {
                        encryptedConnector.send(
                            Response.serialize(
                                Response()
                                    .withResponseCode(Result.VERSION_NOT_MATCH)
                            )
                        )
                        break
                    }
                    val stringToken = request.getContent("token")
                    val (code, permissions) = doAuth(stringToken)
                    logger.debug("$code has following permissions: ${permissions?.joinToString(", ")}")
                    val isCodeExist = permissions != null
                    if (isCodeExist) {
                        val randomKey = Util.generateRandomString(32)
                        encryptedConnector.send(
                            Response.serialize(
                                Response()
                                    .withResponseCode(Result.OK)
                                    .withContentPair("key", randomKey)
                                    .withContentPair("serverName", config.serverName)
                            )
                        )
                        logger.info("Starting SessionServer for #${socket.getInetAddress()}:${socket.port}")
                        logger.debug("Key of {}:{} is {}", socket.inetAddress, socket.port, randomKey)
                        val session =
                            SessionServer(
                                Session(
                                    socket,
                                    randomKey.toByteArray(StandardCharsets.UTF_8)
                                ), permissions!!
                            )
                        session.start()
                        break
                    } else {
                        logger.warn("Permission code $code not exist")
                        encryptedConnector.send(
                            Response.serialize(
                                Response()
                                    .withResponseCode(Result.PERMISSION_DENIED)
                            )
                        )
                    }
                    break
                }
                line = encryptedConnector.readLine()
            }
        } catch (e: Throwable) {
            logger.error("Error occurred while handling Session login request: $e")
            logger.debug("Exception: ", e)
            try {
                socket.close()
            } catch (ex: IOException) {
                logger.error("Error occurred while closing crashed session: $ex")
            }
        }
    }
}
