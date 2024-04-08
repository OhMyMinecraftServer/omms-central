package icu.takeneko.omms.central.config

import icu.takeneko.omms.central.network.ChatbridgeImplementation
import icu.takeneko.omms.central.util.Util
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import kotlin.io.path.*

object Config {
    private val json = Json {
        this.prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
    private val file = Path(Util.getWorkingDirString()) / "config.json"
    lateinit var config: ConfigStorage
    private val logger = LoggerFactory.getLogger("Config")

    fun load(): Boolean {
        var ret = true
        if (file.notExists()) {
            logger.info("Writing default config file.")
            writeConfig(ConfigStorage())
            ret = false
        }
        return try {
            config = json.decodeFromString<ConfigStorage>(file.readText())
            save()
            ret
        } catch (e: Exception) {
            logger.error("Read config failed.", e)
            logger.info("Writing default config file.")
            writeConfig(ConfigStorage())
            false
        }
    }

    private inline fun <reified T> writeConfig(obj: T) {
        file.deleteIfExists()
        file.createFile()
        file.writeText(json.encodeToString<T>(obj))
    }

    fun save() {
        writeConfig(config)
    }
}

@Serializable
data class ConfigStorage(
    val port: Int = 50000,
    val serverName: String = "OMMS-Central",
    val httpPort: Int = 50001,
    val rateLimit: Int = 1000,
    val authorisedController: List<String> = listOf(),
    val chatbridgeImplementation: ChatbridgeImplementation = ChatbridgeImplementation.UDP,
    val apiAccessKey: String = "XX" + Util.generateRandomString(30)
)