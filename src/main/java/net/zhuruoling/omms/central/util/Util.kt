package net.zhuruoling.omms.central.util

import net.zhuruoling.omms.central.controller.Controller
import net.zhuruoling.omms.central.controller.ControllerImpl
import net.zhuruoling.omms.central.whitelist.Whitelist
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory

fun whitelistPrettyPrinting(whitelist: Whitelist): String{
    return """
        - Whitelist: ${whitelist.name}
            ${whitelist.players.joinToString(separator = ", ")}
    """.trimIndent()
}

fun controllerPrettyPrinting(controllerImpl: Controller): String{
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

fun toTypedArray(list: MutableList<Int>): Array<Int>{
    return list.toTypedArray()
}

fun <T> mutableListOf(vararg elements:T): MutableList<T>{
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