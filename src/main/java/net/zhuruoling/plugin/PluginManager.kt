package net.zhuruoling.plugin

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.command.Command
import net.zhuruoling.command.CommandManager
import net.zhuruoling.util.PluginAlreadyLoadedException
import net.zhuruoling.util.PluginNotExistException
import net.zhuruoling.util.PluginNotLoadedException
import net.zhuruoling.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ExecutorCompletionService
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import kotlin.math.log

object PluginManager {
    val logger: Logger = LoggerFactory.getLogger("PluginManger")
    var pluginFileList = ArrayList<String>()
    var manager = ScriptEngineManager()
    var pluginTable = HashMap<String, PluginInstance>()
    val gson: Gson = GsonBuilder().serializeNulls().create()
    var pluginCommandTable: HashMap<String,ArrayList<String>> = java.util.HashMap()
    fun init() {
        pluginTable.clear()
        pluginFileList.clear()
        var files = Files.list(Path.of(Util.joinFilePaths("plugins")))
        var x = 0
        files.forEach {
            if (it.toFile().extension == "js") {
                x++
                pluginFileList.add(it.toFile().absolutePath)
            }
        }
        logger.debug(x.toString())
        logger.debug(pluginFileList.toString())
        pluginFileList.forEach {
            val engine: ScriptEngine = manager.getEngineByName("JavaScript")
            engine.eval(FileReader(it))
            val pluginMetadata = ((engine as Invocable).invokeFunction("getMetadata")) as String
            logger.info("Metadata of plugin $it is $pluginMetadata")
            val metadata = gson.fromJson(pluginMetadata, PluginMetadata::class.javaObjectType) as PluginMetadata
            if (pluginTable.contains(metadata.id)) {
                val pluginId = metadata.id
                logger.error("Plugin $pluginId existed.")
                return
            }
            pluginTable[metadata.id] = PluginInstance(engine as Invocable, PluginStatus.UNLOADED, metadata)
        }
    }

    fun registerPluginCommand(pluginName: String, command: String) {
        if (pluginCommandTable.containsKey(pluginName)){
            val list = pluginCommandTable[pluginName]
            list?.add(command)
            pluginCommandTable[pluginName] = list as java.util.ArrayList<String>
            return
        }
        throw PluginNotExistException("The specified plugin $pluginName does not exist.")

    }

    fun loadAll() {
        pluginTable.forEach {
            load(it.key)
        }
    }

    fun unloadAll() {
        pluginTable.forEach {
            unload(it.key)
        }
    }

    fun execute(pluginName: String,functionName:String, command: Command, serverInterface: RequestServerInterface){
        val pluginInstance = pluginTable[pluginName] ?: throw PluginNotExistException("Plugin $pluginName does not exist.")
        if (pluginInstance.pluginStatus == PluginStatus.UNLOADED)
            throw PluginNotLoadedException("Plugin $pluginName hasn't been loaded.")
        pluginInstance.invocable?.invokeFunction(functionName, serverInterface, command)

    }

    fun load(pluginName: String): Unit {
        logger.info("Loading Plugin:%s".format(pluginName))
        val pluginInstance = pluginTable[pluginName]
        pluginCommandTable[pluginName] = ArrayList()
        val initServerInterface = InitServerInterface(pluginName)
        if (pluginInstance != null) {
            if (pluginInstance.pluginStatus == PluginStatus.LOADED){
                throw PluginAlreadyLoadedException("Plugin $pluginName already loaded.")
            }
            pluginInstance.invocable?.invokeFunction("onLoad", initServerInterface)
            pluginInstance.pluginStatus = PluginStatus.LOADED
        }
        else{
            throw PluginNotExistException("Plugin $pluginName not exist.")
        }
    }

    fun unload(pluginName: String): Unit {
        logger.info("Unloading Plugin:%s".format(pluginName))
        val pluginInstance = pluginTable[pluginName]
        val commands = pluginCommandTable[pluginName]
        val initServerInterface = InitServerInterface(pluginName)
        if (pluginInstance != null) {
            if (pluginInstance.pluginStatus != PluginStatus.LOADED){
                throw PluginNotLoadedException("Plugin $pluginName hasn't been loaded.")
            }
            try {
                pluginInstance.invocable?.invokeFunction("onUnload", initServerInterface)
            }
            catch (e: java.lang.Exception){
                e.printStackTrace()
            }
            commands?.forEach {
                CommandManager.unregisterCommand(it)
            }
            pluginInstance.pluginStatus = PluginStatus.UNLOADED
        }
        else{
            throw PluginNotExistException("Plugin $pluginName not exist.")
        }
    }

    fun reload(pluginId: String): Unit {
        unload(pluginId)
        load(pluginId)
        init()
    }



}