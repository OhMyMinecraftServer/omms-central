package icu.takeneko.omms.central.network.session.server

import com.google.gson.GsonBuilder
import icu.takeneko.omms.central.config.Config.config
import icu.takeneko.omms.central.network.session.EncryptedMessageChannel
import icu.takeneko.omms.central.network.session.Session
import icu.takeneko.omms.central.network.session.request.LoginRequest
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.network.session.response.Result
import icu.takeneko.omms.central.util.Util
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginSession(
    private val socket: Socket,
    private val receiveChannel: ByteReadChannel,
    private val writeChannel: ByteWriteChannel
) {
    private val channel: EncryptedMessageChannel
    private val logger = LoggerFactory.getLogger("InitSession")
    private val gson = GsonBuilder().serializeNulls().create()


    init {
        val date = LocalDateTime.now()
        var key = date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))
        key = Util.base64Encode(Util.base64Encode(key!!))
        logger.debug("key: $key")
        channel = EncryptedMessageChannel(receiveChannel, writeChannel, key = key.encodeToByteArray())
        logger.info("Client: " + socket.remoteAddress + " connected.")
    }

    suspend fun processLogin(): SessionServer? {
        try {
            while (true) {
                val line = channel.readLine()
                val request = gson.fromJson(line, LoginRequest::class.java)
                logger.debug("Got request:{}", request)
                if (request.request == "PING") {
                    if (request.version != Util.PROTOCOL_VERSION) {
                        channel.send(
                            Response.serialize(
                                Response().withResponseCode(Result.VERSION_NOT_MATCH)
                                    .withContentPair("version", Util.PROTOCOL_VERSION.toString())
                            )
                        )
                        return null
                    }
                    val stringToken = request.getContent("token")
                    val (code, permissions) = doAuth(stringToken)
                    logger.debug("$code has following permissions: ${permissions?.joinToString(", ")}")
                    val isCodeExist = permissions != null
                    if (isCodeExist) {
                        val randomKey = Util.generateRandomString(32)
                        channel.send(
                            Response.serialize(
                                Response()
                                    .withResponseCode(Result.OK)
                                    .withContentPair("key", randomKey)
                                    .withContentPair("serverName", config.serverName)
                            )
                        )
                        logger.info("Starting SessionServer for ${socket.remoteAddress}")
                        logger.debug("Key of {} is {}", socket.remoteAddress, randomKey)
                        return SessionServer(
                            Session(
                                socket,
                                randomKey.toByteArray(StandardCharsets.UTF_8),
                                receiveChannel,
                                writeChannel
                            ), permissions!!
                        )
                    } else {
                        logger.warn("Permission code $code not exist")
                        channel.send(
                            Response.serialize(
                                Response().withResponseCode(Result.PERMISSION_DENIED)
                            )
                        )
                        return null
                    }
                } else {
                    return null
                }
            }
        } catch (e: Throwable) {
            logger.error("Error occurred while handling Session login request: $e")
            logger.debug("Exception: ", e)
            return null
        }
    }
}
