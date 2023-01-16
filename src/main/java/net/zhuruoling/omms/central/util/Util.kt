package net.zhuruoling.omms.central.util

import net.zhuruoling.omms.central.controller.Controller
import net.zhuruoling.omms.central.whitelist.Whitelist

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

fun toTypedArray(list: MutableList<Int>): Array<Int>{
    return list.toTypedArray()
}

fun <T> mutableListOf(vararg elements:T): MutableList<T>{
    return kotlin.collections.mutableListOf(*elements)
}