package net.zhuruoling.omms.central.foo

import org.apache.tools.ant.taskdefs.Classloader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J
import java.nio.file.FileSystems

val logger: Logger = LoggerFactory.getLogger("Test")




fun main() {
    FileSystems.getDefault().fileStores.forEach{

        println("$it type: ${it.type()}  name: ${it.name()}  dir: ${it}")
    }
}
