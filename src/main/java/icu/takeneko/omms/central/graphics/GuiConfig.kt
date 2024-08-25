package icu.takeneko.omms.central.graphics

import icu.takeneko.omms.central.config.Config
import icu.takeneko.omms.central.util.Util
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.path.*

object GuiConfig {
    private val json = Json {
        this.prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
    private val file = Path(Util.getWorkingDirString()) / "guiConfig.json"
    var config = GuiConfigData()
        private set

    fun load(): Boolean {
        var ret = true
        if (file.notExists()) {
            writeConfig(GuiConfigData())
            ret = false
        }
        return try { 
            config = json.decodeFromString<GuiConfigData>(file.readText())
            save()
            ret
        } catch (e: Exception) {
            writeConfig(GuiConfigData())
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
    
    @kotlinx.serialization.Serializable
    data class GuiConfigData(
        var autoScroll:Boolean = true
    )
}