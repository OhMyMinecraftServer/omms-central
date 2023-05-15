package net.zhuruoling.omms.central.plugin

import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.foo.logger
import net.zhuruoling.omms.central.script.ScriptManager
import net.zhuruoling.omms.central.util.Util
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

object PluginManager {
    private var pluginMap = LinkedHashMap<String, PluginInstance>()
    private var pluginFileList = arrayListOf<String>()
    private lateinit var classLoader: URLClassLoader
    fun init() {
        if (GlobalVariable.noPlugins) {
            ScriptManager.logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        pluginMap.clear()
        pluginFileList.clear()
        Files.list(Path.of(Util.joinFilePaths("plugins")))
            .filter { it.toFile().extension == "jar" }.forEach {
                pluginFileList += it.absolutePathString()
            }
        classLoader = URLClassLoader(pluginFileList.stream().map { URL("file://$it") }.toList().toTypedArray())
        pluginFileList.forEach {
            PluginInstance(classLoader, Path(it)).run {
                loadJar()
                if (pluginMetadata.id in pluginMap) {
                    logger.error("Plugin $it has a same id with plugin ${pluginMap[pluginMetadata.id]!!.pluginPathUrl.path}")
                    return@forEach
                }
                loadPluginClasses()
                this.pluginMetadata.id to this
            }.apply { pluginMap += this }
        }
    }

    fun loadAll() {

    }

    operator fun get(id: String): PluginInstance? {
        return pluginMap[id]
    }

}