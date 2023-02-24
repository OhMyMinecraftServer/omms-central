package net.zhuruoling.omms.central.foo

import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.controller.console.ControllerConsole
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J

fun main() {
    //SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()
    ControllerManager.init()
    val console = ControllerConsole(
        ControllerManager.getControllerByName("out_survival")!!.controller
    )
    console.start()
}
