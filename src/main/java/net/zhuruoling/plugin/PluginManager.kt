package net.zhuruoling.plugin

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.command.Command
import net.zhuruoling.command.CommandManager
import net.zhuruoling.main.Flags
import net.zhuruoling.util.PluginAlreadyLoadedException
import net.zhuruoling.util.PluginNotExistException
import net.zhuruoling.util.PluginNotLoadedException
import net.zhuruoling.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

object PluginManager {
    val logger: Logger = LoggerFactory.getLogger("PluginManger")
    var pluginFileList = ArrayList<String>()
    var pluginTable = HashMap<String, GroovyPluginInstance>()
    val gson: Gson = GsonBuilder().serializeNulls().create()
    var pluginCommandTable: HashMap<String,ArrayList<String>> = java.util.HashMap()
    fun init() {
        if (Flags.noPlugins){
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        pluginTable.clear()
        pluginFileList.clear()
        val files = Files.list(Path.of(Util.joinFilePaths("plugins")))
        files.forEach {
            if (it.toFile().extension == "groovy") {
                pluginFileList.add(it.toFile().absolutePath)
            }
        }
        logger.debug(pluginFileList.toString())
        pluginFileList.forEach {
            val pluginInstance = GroovyPluginInstance(it)
            val metadata = pluginInstance.initPlugin()
            logger.info("Metadata of plugin $it is $metadata")
            if (pluginTable.contains(metadata.id)) {
                val pluginId = metadata.id
                logger.error("Plugin $it got a conflicted plugin id with plugin ${pluginTable[pluginId]?.pluginFilePath}")
                return@forEach
            }
            pluginInstance.pluginStatus = PluginStatus.UNLOADED
            pluginTable[metadata.id] = pluginInstance
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
        if (Flags.noPlugins){
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        pluginTable.forEach {
            load(it.key)
        }
    }

    fun unloadAll() {
        if (Flags.noPlugins){
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        pluginTable.forEach {
            unload(it.key)
        }
    }

    fun execute(pluginName: String,functionName:String, command: Command, serverInterface: RequestServerInterface){
        val pluginInstance = pluginTable[pluginName] ?: throw PluginNotExistException("Plugin $pluginName does not exist.")
        if (pluginInstance.pluginStatus == PluginStatus.UNLOADED)
            throw PluginNotLoadedException("Plugin $pluginName hasn't been loaded.")
        pluginInstance.invokeMethod(functionName, serverInterface, command)

    }

    fun load(pluginName: String) {
        logger.info("Loading Plugin:%s".format(pluginName))
        val pluginInstance = pluginTable[pluginName]
        pluginCommandTable[pluginName] = ArrayList()
        val initServerInterface = LifecycleServerInterface(pluginName)
        if (pluginInstance != null) {
            if (pluginInstance.pluginStatus == PluginStatus.LOADED){
                throw PluginAlreadyLoadedException("Plugin $pluginName already loaded.")
            }
            pluginInstance.invokeMethod("onLoad", initServerInterface)
            pluginInstance.pluginStatus = PluginStatus.LOADED
        }
        else{
            throw PluginNotExistException("Plugin $pluginName not exist.")
        }
    }

    fun unload(pluginName: String) {
        logger.info("Unloading Plugin:%s".format(pluginName))
        val pluginInstance = pluginTable[pluginName]
        val commands = pluginCommandTable[pluginName]
        val initServerInterface = LifecycleServerInterface(pluginName)
        if (pluginInstance != null) {
            if (pluginInstance.pluginStatus != PluginStatus.LOADED){
                throw PluginNotLoadedException("Plugin $pluginName hasn't been loaded.")
            }
            try {
                pluginInstance.invokeMethod("onUnload", initServerInterface)
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

    fun reload(pluginId: String) {
        if (Flags.noPlugins){
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        unload(pluginId)
        init()
        load(pluginId)
    }
}