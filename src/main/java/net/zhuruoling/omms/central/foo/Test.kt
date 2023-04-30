package net.zhuruoling.omms.central.foo

import net.zhuruoling.omms.central.plugin.PluginInstance
import net.zhuruoling.omms.central.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J

val logger: Logger = LoggerFactory.getLogger("Test")


fun main() {
    SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()
    PluginInstance(Util.joinFilePaths("plugins","jarTest-0.0.1.jar")).use {
        it.run {
            loadJar()
            onLoad()
            onUnload()
        }
    }
}
