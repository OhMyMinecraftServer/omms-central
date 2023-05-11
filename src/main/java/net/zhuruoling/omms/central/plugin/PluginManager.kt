package net.zhuruoling.omms.central.plugin

import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.old.plugin.PluginManager
import net.zhuruoling.omms.central.util.Util
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

object PluginManager {
    private var pluginMap = LinkedHashMap<String, PluginInstance>()
    private var pluginFileList = arrayListOf<String>()

    fun init() {
        if (GlobalVariable.noPlugins) {
            PluginManager.logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        pluginMap.clear()
        pluginFileList.clear()
        Files.list(Path.of(Util.joinFilePaths("plugins")))
            .filter{it.toFile().extension == "jar"}.forEach {
            pluginFileList += it.absolutePathString()
        }
    }

    fun loadAll(){

    }

    fun unloadAll(){

    }


    operator fun get(id:String): PluginInstance?{
        return pluginMap[id]
    }

}