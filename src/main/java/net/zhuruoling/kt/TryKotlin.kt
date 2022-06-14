package net.zhuruoling.kt

import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory
object TryKotlin {
    fun printOS(){
        val logger = LoggerFactory.getLogger("ConfigReader")
        val os = ManagementFactory.getOperatingSystemMXBean()
        logger.info(String.format("Uranium qLink is running on %s %s %s",os.name, os.arch, os.version))
    }
}

