package icu.takeneko.omms.central.util

import icu.takeneko.omms.central.controller.Controller
import icu.takeneko.omms.central.controller.ControllerImpl
import icu.takeneko.omms.central.controller.Status
import icu.takeneko.omms.central.fundation.Constants
import icu.takeneko.omms.central.whitelist.Whitelist
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
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
        return "${Constants.PRODUCT_NAME} $version (${BuildProperties["branch"]}:${
            BuildProperties["commitId"]?.substring(0, 7)
        } $buildTime)"
    }

fun printRuntimeEnv() {
    val logger = LoggerFactory.getLogger("Util")
    val os = ManagementFactory.getOperatingSystemMXBean()
    val runtime = ManagementFactory.getRuntimeMXBean()
    logger.info("$versionInfoString is running on ${os.name} ${os.arch} ${os.version} at pid ${runtime.pid}")
}

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