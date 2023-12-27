package net.zhuruoling.omms.central.network.pair

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.util.*
import net.zhuruoling.omms.central.command.CommandSourceStack
import net.zhuruoling.omms.central.config.Config.config
import net.zhuruoling.omms.central.util.Manager
import net.zhuruoling.omms.central.util.Util
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import kotlin.io.path.Path

data class PairConfig(val enabled: Boolean, val preloadControllerConfig: PreloadControllerConfig)

data class ControllerConfig(
    val controllerId: String,
    val usesWhitelist: String,
    val preloadControllerConfig: PreloadControllerConfig
)

object PairManager : Manager() {
    private var preloadConfig = PreloadControllerConfig()
    var enabled = false
    private val pairCodeMap = mutableMapOf<String, ControllerConfig>()

    override fun init() {
        val path = Path(Util.joinFilePaths("pair.json"))
        if (!Files.exists(path)) {
            Files.createFile(path)
            val writer = FileWriter(path.toFile())
            val pairConfig = PairConfig(
                true, PreloadControllerConfig(
                    "localhost:${config.httpPort}",
                    "GLOBAL",
                    "bot_",
                    "_bot",
                    true,
                    true,
                    true,
                    true,
                    listOf(ServerMapping("whitelist_name", "server_name_in_your_proxy", "display_name"))
                )
            )
            Gson().toJson(pairConfig, writer)
            writer.close()
        }
        val reader = FileReader(path.toFile())
        val pairConfig = GsonBuilder().serializeNulls().create().fromJson(reader, PairConfig::class.javaObjectType)
        enabled = pairConfig.enabled
        preloadConfig = pairConfig.preloadControllerConfig
    }


    fun create(controllerId: String, usesWhitelist: String): String {
        val pairCode = Util.generateRandomString(5, false, false).toUpperCasePreservingASCIIRules()
        val controllerConfig = ControllerConfig(controllerId, usesWhitelist, preloadConfig)
        this.pairCodeMap[pairCode] = controllerConfig
        return pairCode
    }

    operator fun get(pairCode: String): ControllerConfig {
        val controllerConfig =
            this.pairCodeMap[pairCode] ?: throw IllegalArgumentException("Pair code($pairCode) not exist")
        this.pairCodeMap.remove(pairCode, controllerConfig)
        return controllerConfig
    }

    fun consoleMakePair(commandSourceStack: CommandSourceStack){

    }
}