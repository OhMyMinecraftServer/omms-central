package net.zhuruoling.omms.central.util

import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.controller.Controller
import net.zhuruoling.omms.central.controller.ControllerImpl
import net.zhuruoling.omms.central.whitelist.Whitelist
import org.jline.reader.impl.history.DefaultHistory
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory
import java.net.URL

fun whitelistPrettyPrinting(whitelistImpl: Whitelist): String {
    return """
        - Whitelist: ${whitelistImpl.name}
            ${whitelistImpl.players.joinToString(separator = ", ")}
    """.trimIndent()
}

fun controllerPrettyPrinting(controller: Controller): String {
    if (controller is ControllerImpl)
        return """
        - Controller: ${controller.name}
            isStatusQueryable: ${controller.isStatusQueryable}
            type: ${controller.type}
            executable: ${controller.executable}
            launchParameters: ${controller.launchParams}
            workingDirectory: ${controller.workingDir}
    """.trimIndent()
    else
        return """
        - Controller: ${controller.name}
            type: ${controller.type}
            isStatusQueryable: ${controller.isStatusQueryable}
    """.trimIndent()

}

fun toTypedArray(list: MutableList<Int>): Array<Int> {
    return list.toTypedArray()
}

fun toTypedArray(list: MutableList<URL>): Array<URL> {
    return list.toTypedArray()
}

fun <T> mutableListOf(vararg elements: T): MutableList<T> {
    return kotlin.collections.mutableListOf(*elements)
}

fun printRuntimeEnv() {
    val logger = LoggerFactory.getLogger("Util")
    val os = ManagementFactory.getOperatingSystemMXBean()
    val runtime = ManagementFactory.getRuntimeMXBean()
    val version = BuildProperties["version"]
    logger.info("${Util.PRODUCT_NAME} $version is running on ${os.name} ${os.arch} ${os.version} at pid ${runtime.pid}",)
}

fun getOrCreateControllerHistroy(controllerId: String): DefaultHistory {
    return GlobalVariable.controllerConsoleHistory[controllerId]
        ?: DefaultHistory().run { GlobalVariable.controllerConsoleHistory[controllerId] = this;this }
}
/**
 * copied from Kotlin Native
 */
object NativeBase64Encoder {

    private const val BASE64_ALPHABET: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    private const val BASE64_MASK: Byte = 0x3f
    private const val BASE64_PAD: Char = '='
    private val BASE64_INVERSE_ALPHABET = IntArray(256) {
        BASE64_ALPHABET.indexOf(it.toChar())
    }
    private fun Int.toBase64(): Char = BASE64_ALPHABET[this]

    fun encode(src: ByteArray): ByteArray {
        fun ByteArray.getOrZero(index: Int): Int = if (index >= size) 0 else get(index).toInt()
        // 4n / 3 is expected Base64 payload
        val result = ArrayList<Byte>(4 * src.size / 3)
        var index = 0
        while (index < src.size) {
            val symbolsLeft = src.size - index
            val padSize = if (symbolsLeft >= 3) 0 else (3 - symbolsLeft) * 8 / 6
            val chunk = (src.getOrZero(index) shl 16) or (src.getOrZero(index + 1) shl 8) or src.getOrZero(index + 2)
            index += 3
            for (i in 3 downTo padSize) {
                val char = (chunk shr (6 * i)) and BASE64_MASK.toInt()
                result.add(char.toBase64().code.toByte())
            }
            // Fill the pad with '='
            repeat(padSize) { result.add(BASE64_PAD.code.toByte()) }
        }

        return result.toByteArray()
    }
}