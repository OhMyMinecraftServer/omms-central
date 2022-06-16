package net.zhuruoling.plugins

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.util.PluginNotExistException
import net.zhuruoling.util.PluginNotLoadedException
import net.zhuruoling.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Executors
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

object PluginManager {
    val logger: Logger = LoggerFactory.getLogger("PluginManger")
    var pluginFileList = ArrayList<String>()
    var manager = ScriptEngineManager()
    var pluginTable = HashMap<String, PluginInstance>()
    val gson: Gson = GsonBuilder().serializeNulls().create()
    val executor = Executors.newFixedThreadPool(5);
    fun init() {
        val files = Files.list(Path.of(Util.joinFilePaths("plugins")))
        files.forEach {
            if (it.toFile().extension == "js")
                pluginFileList.add(it.toFile().absolutePath)
        }
        logger.debug(pluginFileList.toString())
        pluginFileList.forEach {
            val engine: ScriptEngine = manager.getEngineByName("JavaScript")
            engine.eval(FileReader(it))
            val pluginMetadata = ((engine as Invocable).invokeFunction("getMetadata")) as String
            logger.debug("Metadata of plugin $it is $pluginMetadata")
            val metadata = gson.fromJson(pluginMetadata, PluginMetadata::class.javaObjectType) as PluginMetadata
            if (pluginTable.contains(metadata.id)) {
                val pluginId = metadata.id
                logger.error("Plugin $pluginId existed.")
                return
            }
            pluginTable[metadata.id] = PluginInstance(engine as Invocable, PluginStatus.UNLOADED, metadata)
        }
    }


    fun loadAll() {
        pluginTable.forEach {
            logger.info("Loading Plugin:%s".format(it.key))
            val pluginInstance = it.value
            val initServerInterface = InitServerInterface(it.key)
            logger.debug(pluginInstance.toString())
            pluginInstance.invocable?.invokeFunction("onLoad", initServerInterface)
        }
    }

    fun unloadAll() {
        pluginTable.forEach {
            logger.info("Loading Plugin:%s".format(it.key))
            val pluginInstance = it.value
            val initServerInterface = InitServerInterface(it.key)
            pluginInstance.invocable?.invokeFunction("onUnload", initServerInterface)
        }
    }

    fun execute(pluginName: String,functionName:String, vararg args:Any?){
        val pluginInstance = pluginTable[pluginName] ?: throw PluginNotExistException("Plugin $pluginName does not exist.")
        if (pluginInstance.pluginStatus == PluginStatus.UNLOADED)
            throw PluginNotLoadedException("Plugin $pluginName hasn't been loaded.")
        pluginInstance.invocable?.invokeFunction(functionName, args)
    }
    fun load(pluginName: String): Unit {

    }

    fun unload(pluginId: String): Unit {

    }

    fun reload(pluginId: String): Unit {
        unload(pluginId = pluginId)
        load(pluginName = PluginUtils.getPluginFileNameById(pluginId))
    }

    suspend fun reloadAll(): Unit {
        unloadAll()
        loadAll()
    }


}