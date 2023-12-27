package net.zhuruoling.omms.central.foo

import net.zhuruoling.omms.central.database.DatabaseConnection
import net.zhuruoling.omms.central.util.Util
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("TestMain")
    DatabaseConnection.init()
    DatabaseConnection.createPlayer("wdnmd", mutableListOf("out", "in"), mutableListOf("survival", "creative"))
    DatabaseConnection.createPlayer(
        Util.generateRandomString(8),
        mutableListOf("out", "in"),
        mutableListOf("survival", "creative")
    )
    DatabaseConnection.getAllPlayer().forEach {
        logger.info(it.toString())
    }
    DatabaseConnection.deletePlayer("wdnmd")
    DatabaseConnection.close()
}
