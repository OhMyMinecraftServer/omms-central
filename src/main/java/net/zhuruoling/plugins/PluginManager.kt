package net.zhuruoling.plugins

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sun.jdi.event.StepEvent
import net.zhuruoling.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path
import java.util.Collections
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory
import javax.script.ScriptEngineManager

object PluginManager {
    val logger: Logger = LoggerFactory.getLogger("PluginManger")
    var pluginFileList = ArrayList<String>()
    var manager = ScriptEngineManager()
    var pluginTable = HashMap<String,Invocable>()
    val gson: Gson = GsonBuilder().serializeNulls().create()
    fun init(): Unit {
        val files = Files.list(Path.of(Util.joinFilePaths("plugins")))
        files.forEach{
            if (it.toFile().extension == "js")
                pluginFileList.add(it.toFile().absolutePath)
        }
        logger.debug(pluginFileList.toString())
        pluginFileList.forEach{
            val engine: ScriptEngine = manager.getEngineByName("JavaScript")
            engine.eval(FileReader(it))
            val pluginMetadata = ((engine as Invocable).invokeFunction("getMetadata")) as String
            logger.debug("Metadata of plugin $it is $pluginMetadata")
            val metadata = gson.fromJson(pluginMetadata,PluginMetadata::class.javaObjectType) as PluginMetadata
            if (pluginTable.contains(metadata.id)){
                val pluginId = metadata.id
                logger.error("Plugin $pluginId existed.")
                return
            }
            pluginTable[metadata.id] = (engine as Invocable)
        }
    }

    fun loadAll(): Unit {
        pluginTable.forEach{
            logger.info("Loading Plugin:%s".format(it.key))
            val pluginInstance = it.value
            val initServerInterface = InitServerInterface(it.key)
            pluginInstance.invokeFunction("onLoad", initServerInterface)
        }
    }

    fun unloadAll(): Unit {
        pluginTable.forEach{
            logger.info("Loading Plugin:%s".format(it.key))
            val pluginInstance = it.value
            val initServerInterface = InitServerInterface(it.key)
            pluginInstance.invokeFunction("onUnload", initServerInterface)
        }
    }

    fun load(pluginName: String): Unit {

    }

    fun unload(pluginId: String): Unit {

    }

    fun reload(pluginId: String): Unit {
        unload(pluginId = pluginId)
        load(pluginName = PluginUtils.getPluginFileNameById(pluginId))
    }

    fun reloadAll(): Unit {
        unloadAll()
        loadAll()
    }


}