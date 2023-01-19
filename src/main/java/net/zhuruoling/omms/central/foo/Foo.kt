package net.zhuruoling.omms.central.foo

import net.zhuruoling.omms.central.util.Util
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory

object Foo {
    fun bar() {
        //SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
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

fun main() {
    for (s in arrayOf("String", "Float","Int","Long","Boolean", "StringSet")){
        println("fun put$s(key: String, value: ${if (s == "StringSet") "MutableSet<String>" else s}): PreferencesStorage{\n" +
                "        editor.put$s(key, value)\n" +
                "        return this\n" +
                "    }")
    }
}



