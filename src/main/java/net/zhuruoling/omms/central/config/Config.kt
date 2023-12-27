package net.zhuruoling.omms.central.config

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.zhuruoling.omms.central.network.ChatbridgeImplementation
import net.zhuruoling.omms.central.util.Util
import org.slf4j.LoggerFactory
import kotlin.io.path.*

object Config {
    private val json = Json {
        this.prettyPrint = true
        encodeDefaults = true
    }
    private val file = Path(Util.getWorkingDir()) / "config.json"
    lateinit var config: ConfigStorage
    private val logger = LoggerFactory.getLogger("Config")

    fun load():Boolean{
        var ret = false
        if (file.notExists()){
            logger.info("Writing default config file.")
            writeConfig(ConfigStorage())
            ret = true
        }
        return try {
            config = json.decodeFromString<ConfigStorage>(file.readText())
            save()
            ret
        }catch (e:Exception){
            logger.error("Read config failed.", e)
            logger.info("Writing default config file.")
            writeConfig(ConfigStorage())
            false
        }
    }

    private inline fun <reified T> writeConfig(obj:T){
        file.deleteIfExists()
        file.createFile()
        file.writeText(json.encodeToString<T>(obj))
    }

    fun save(){
        writeConfig(config)
    }
}

data class ConfigStorage(
    val port: Int = 50000,
    val serverName: String = "OMMS-Central",
    val httpPort: Int = 50001,
    val rateLimit: Int = 1000,
    val authorisedController: List<String> = listOf(),
    var chatbridgeImplementation: ChatbridgeImplementation = ChatbridgeImplementation.UDP
)