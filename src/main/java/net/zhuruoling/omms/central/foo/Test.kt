package net.zhuruoling.omms.central.foo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import cn.korostudio.interaction.base.BaseClient
import cn.korostudio.interaction.base.data.Server
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J

val logger: Logger = LoggerFactory.getLogger("Test")

fun main() {
    SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()
    val server = Server().apply {
        port = 50001
        id = "omms-central"
        address = "localhost"
    }
    val client = BaseClient.init(server)
}

