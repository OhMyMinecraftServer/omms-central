package net.zhuruoling.omms.central.foo

import net.zhuruoling.omms.central.util.Util
import org.apache.commons.io.FileSystem
import org.slf4j.LoggerFactory
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J
import java.io.File
import java.lang.management.ManagementFactory

object Foo {
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
}


fun main(args: Array<String>) {
    //SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()
    val logger = LoggerFactory.getLogger("TestMain")
    logger.debug("hello")
    logger.info("yee")
    logger.warn("wtf")
    logger.error("wdnmd")

}
