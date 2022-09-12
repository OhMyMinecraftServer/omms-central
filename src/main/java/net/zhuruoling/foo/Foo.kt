package net.zhuruoling.foo

import net.zhuruoling.util.Util
import org.slf4j.LoggerFactory
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J
import java.lang.management.ManagementFactory

object Foo {
    fun bar(){
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        val logger = LoggerFactory.getLogger("YEE")
        val os = ManagementFactory.getOperatingSystemMXBean()
        val runtime = ManagementFactory.getRuntimeMXBean()
        logger.info(String.format("${Util.PRODUCT_NAME} is running on %s %s %s at pid %d",os.name, os.arch, os.version, runtime.pid))
    }

}

