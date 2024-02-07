package icu.takeneko.omms.central.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.bytebuddy.agent.ByteBuddyAgent
import icu.takeneko.omms.central.GlobalVariable
import icu.takeneko.omms.central.controller.Controller
import icu.takeneko.omms.central.controller.ControllerImpl
import icu.takeneko.omms.central.controller.Status
import icu.takeneko.omms.central.whitelist.Whitelist
import org.jline.reader.impl.history.DefaultHistory
import org.slf4j.LoggerFactory
import java.lang.instrument.Instrumentation
import java.lang.management.ManagementFactory
import java.net.URL
import java.util.*

fun whitelistPrettyPrinting(whitelistImpl: Whitelist): String {
    return """
        - Whitelist: ${whitelistImpl.name}
            ${whitelistImpl.players.joinToString(separator = ", ")}
    """.trimIndent()
}

fun controllerPrettyPrinting(controller: Controller): String {
    return if (controller is ControllerImpl)
        """
        - Controller: ${controller.name}
            isStatusQueryable: ${controller.isStatusQueryable}
            displayName: ${controller.displayName}
            type: ${controller.type}
    """.trimIndent()
    else
        """
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

val versionInfoString: String
    get() {
        val version = BuildProperties["version"]
        val buildTimeMillis = BuildProperties["buildTime"]?.toLong() ?: 0L
        val buildTime = Date(buildTimeMillis)
        return "${Util.PRODUCT_NAME} $version (${BuildProperties["branch"]}:${
            BuildProperties["commitId"]?.substring(0, 7)
        } $buildTime)"
    }

fun printRuntimeEnv() {
    val logger = LoggerFactory.getLogger("Util")
    val os = ManagementFactory.getOperatingSystemMXBean()
    val runtime = ManagementFactory.getRuntimeMXBean()
    logger.info("$versionInfoString is running on ${os.name} ${os.arch} ${os.version} at pid ${runtime.pid}")
}

fun getOrCreateControllerHistory(controllerId: String): DefaultHistory {
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

object InstrumentationAccess {
    val instrumentation: Instrumentation by lazy {
        ByteBuddyAgent.install()
    }
}

//fun Any.toStringMap():Map<String,String>{
//    return buildMap {
//        this@toStringMap::class.members.filterIsInstance<KProperty<*>>().forEach { p ->
//                println(p.getter.parameters.joinToString { it.toString() })
//                this[p.name] = p.getter.call(this@toStringMap).toString()
//            }
//    }
//}

fun Status.toStringMap(): Map<String, String> {
    return buildMap {
        this["isAlive"] = isAlive.toString()
        this["isQueryable"] = isQueryable.toString()
        this["name"] = name
        this["type"] = type
        this["playerCount"] = playerCount.toString()
        this["maxPlayerCount"] = maxPlayerCount.toString()
        this["players"] = Json.encodeToString<List<String>>(players)
    }
}