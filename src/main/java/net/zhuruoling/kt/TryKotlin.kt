package net.zhuruoling.kt

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.zhuruoling.system.SystemUtil
import net.zhuruoling.util.Util
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory
import javax.script.ScriptContext
import javax.script.ScriptEngineManager
import kotlin.system.exitProcess

object TryKotlin {
    fun printOS(){
        val logger = LoggerFactory.getLogger("YEE")
        val os = ManagementFactory.getOperatingSystemMXBean()
        logger.info(String.format("${Util.PRODUCT_NAME} is running on %s %s %s",os.name, os.arch, os.version))
        //SystemUtil.print()
        ScriptEngineManager().engineFactories.forEach { println(it.names) }
    }

}

