package net.zhuruoling.omms.central.foo

import io.ktor.util.*
import net.zhuruoling.omms.central.util.Manager
import net.zhuruoling.omms.central.util.Util
import net.zhuruoling.omms.central.whitelist.WhitelistManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import oshi.SystemInfo
import java.util.Base64
import java.util.regex.Pattern

val logger: Logger = LoggerFactory.getLogger("Test")

fun getBIOS(): String {
    val si = SystemInfo()
    val hal = si.hardware
    val cs = hal.computerSystem
    val firmware = cs.firmware
    return firmware.version
}

fun main() {
}

//operator fun <T, R> T.invoke(fn: T.() -> R) = fn(this)

