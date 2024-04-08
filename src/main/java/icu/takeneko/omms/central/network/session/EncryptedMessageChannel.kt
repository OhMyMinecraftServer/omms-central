package icu.takeneko.omms.central.network.session

import icu.takeneko.omms.central.config.Config
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.security.CryptoUtil
import icu.takeneko.omms.central.util.Util
import io.ktor.utils.io.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class EncryptedMessageChannel(
    private val receiveChannel: ByteReadChannel,
    private val writeChannel: ByteWriteChannel,
    private val rateLimit: Int = Config.config.rateLimit,
    val key: ByteArray
) {

    private val logger: Logger = LoggerFactory.getLogger("EncryptedMessageChannel")
    private val enableRateLimit = rateLimit >= 1
    private var time = 0L
    private var count = 0
    suspend fun println(content: String) {
        this.send(content)
    }

    suspend fun receiveRequest(): Request? {
        val line: String = this.readLine() ?: return null
        if (enableRateLimit) {
            if (System.currentTimeMillis() - time >= 1000L) {
                time = System.currentTimeMillis()
                count = 1
            } else {
                count++
                if (count > rateLimit) {
                    throw RateExceedException("Current speed limit exceeded")
                }
            }
        }
        return Util.fromJson(
            line,
            Request::class.java
        )
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

    suspend fun sendResponse(response: Response) {
        val line = Util.toJson(response)
        println(line)
    }
}