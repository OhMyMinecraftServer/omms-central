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

fun controllerPrettyPrinting(controllerImpl: Controller): String {
    if (controllerImpl is ControllerImpl)
        return """
        - Controller: ${controllerImpl.name}
            executable: ${controllerImpl.executable}
            type: ${controllerImpl.type}
            launchParameters: ${controllerImpl.launchParams}
            workingDirectory: ${controllerImpl.workingDir}
            isStatusQueryable: ${controllerImpl.isStatusQueryable}
    """.trimIndent()
    else
        return """
        - Controller: ${controllerImpl.name}
            isStatusQueryable: ${controllerImpl.isStatusQueryable}
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

fun bar() {
    val logger = LoggerFactory.getLogger("YEE")
    val os = ManagementFactory.getOperatingSystemMXBean()
    val runtime = ManagementFactory.getRuntimeMXBean()
    logger.info(
        String.format(
            "${Util.PRODUCT_NAME} is running on %s %s %s at pid %d",
            os.name,
            os.arch,
            os.version,
            runtime.pid
        )
    )
}

fun getOrCreateControllerHistroy(controllerId: String): DefaultHistory {
    return GlobalVariable.controllerConsoleHistory[controllerId]
        ?: DefaultHistory().run { GlobalVariable.controllerConsoleHistory[controllerId] = this;this }
}