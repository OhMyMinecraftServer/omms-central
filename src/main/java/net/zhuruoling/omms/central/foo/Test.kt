package net.zhuruoling.omms.central.foo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J

val logger: Logger = LoggerFactory.getLogger("Test")

class ReflectionMetadata

infix fun <L,R> L.reflexpr(left: R): ReflectionMetadata  {
    return ReflectionMetadata()
}

fun main() {
    SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()

}
