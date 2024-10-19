package icu.takeneko.omms.central.network.session

import icu.takeneko.omms.central.config.Config
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.security.CryptoUtil
import icu.takeneko.omms.central.util.Util
import io.ktor.utils.io.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

class EncryptedMessageChannel(
    private val receiveChannel: ByteReadChannel,
    private val writeChannel: ByteWriteChannel,
    val rateLimit: Int = Config.config.rateLimit,
    val key: ByteArray
) {

    private val logger: Logger = LoggerFactory.getLogger("EncryptedMessageChannel")
    val enableRateLimit = rateLimit >= 1
    var lastReceiveTime = 0L
    var incomingMessageCount = 0
    val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        encodeDefaults = true
    }

    suspend fun receiveRequest(): Request? {
        return receive<Request>()
    }

    suspend inline fun <reified T> receive(): T? where T : Any {
        val line: String = this.readLine() ?: return null
        if (enableRateLimit) {
            if (System.currentTimeMillis() - lastReceiveTime >= 1000L) {
                lastReceiveTime = System.currentTimeMillis()
                incomingMessageCount = 1
            } else {
                incomingMessageCount++
                if (incomingMessageCount > rateLimit) {
                    throw RateExceedException("Current speed limit exceeded")
                }
            }
        }
        return json.decodeFromString<T>(line)
    }

    suspend inline fun <reified T> send(message: T) where T : Any {
        send(json.encodeToString<T>(message))
    }

    suspend fun send(content: String) {
        val data = CryptoUtil.encryptECB(content.toByteArray(StandardCharsets.UTF_8), this.key)
        logger.debug("Sending: $content")
        writeChannel.writeStringUtf8(String(data, StandardCharsets.UTF_8) + "\n")
    }

    suspend fun readLine(): String? {
        val line: String = receiveChannel.readUTF8Line() ?: return null
        logger.debug("Received: $line")
        val data = CryptoUtil.decryptECB(line.toByteArray(StandardCharsets.UTF_8), this.key)
        return String(data, StandardCharsets.UTF_8)
    }
}