package net.zhuruoling.omms.central.foo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J

val logger: Logger = LoggerFactory.getLogger("Test")


fun main() {
    //SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()
    val str = "replace|keep|outline|hollow|destroy"
    str.split("|").forEach {
        print("\"$it\",")
    }
}
