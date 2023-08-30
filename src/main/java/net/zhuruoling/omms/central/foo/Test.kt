package net.zhuruoling.omms.central.foo

import io.ktor.util.*
import net.zhuruoling.omms.central.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import oshi.SystemInfo
import java.text.MessageFormat

val logger: Logger = LoggerFactory.getLogger("Test")

fun getBIOS(): String {
    val si = SystemInfo()
    val hal = si.hardware
    val cs = hal.computerSystem
    val firmware = cs.firmware
    return firmware.version
}


fun main() {
    val systemInfo = SystemInfo()
    println(systemInfo.operatingSystem.isElevated)
    val hal = systemInfo.hardware
    val sn = hal.computerSystem.serialNumber
    val uuid = hal.computerSystem.hardwareUUID
    val networkIfId = hal.networkIFs.run {
        var res = ""
        forEach {
            res += it.macaddr.replace(":","").toUpperCasePreservingASCIIRules()
        }
        res
    }
    println(sn + uuid.replace("-","") + networkIfId)
    println(networkIfId)
    println(Util.toJson(object {
        val i = 114514
        val j = "1919810"
    }))
}

