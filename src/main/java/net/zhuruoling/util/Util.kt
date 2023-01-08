package net.zhuruoling.util

import net.zhuruoling.controller.Controller
import net.zhuruoling.whitelist.Whitelist

fun whitelistPrettyPrinting(whitelist: Whitelist): String{
    return """
        - Whitelist: ${whitelist.name}
            ${whitelist.players.joinToString(separator = ", ")}
    """.trimIndent()
}

fun controllerPrettyPrinting(controller: Controller): String{
    return """
        - Controller: ${controller.name}
            executable: ${controller.executable}
            type: ${controller.type}
            launchParameters: ${controller.launchParams}
            workingDirectory: ${controller.workingDir}
            isStatusQueryable: ${controller.isStatusQueryable}
    """.trimIndent()
}
