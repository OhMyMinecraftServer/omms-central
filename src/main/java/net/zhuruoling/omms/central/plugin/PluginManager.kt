package net.zhuruoling.omms.central.plugin

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.omms.central.console.PluginCommand
import net.zhuruoling.omms.central.main.RuntimeConstants
import net.zhuruoling.omms.central.network.session.request.Request
import net.zhuruoling.omms.central.network.session.request.RequestManager
import net.zhuruoling.omms.central.util.Util
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

object PluginManager {
    val logger: Logger = LoggerFactory.getLogger("PluginManger")
    private var pluginFileList = ArrayList<String>()
    private var pluginTable = HashMap<String, GroovyPluginInstance>()
    val gson: Gson = GsonBuilder().serializeNulls().create()
    private var pluginCommandTable: HashMap<String, ArrayList<String>> = java.util.HashMap()
    fun init() {
        if (RuntimeConstants.noPlugins) {
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
            try {
                val pluginInstance = GroovyPluginInstance(it)
                pluginInstance.initPlugin()
                val metadata = pluginInstance.metadata
                logger.info("Metadata of plugin $it is $metadata")
                if (pluginTable.contains(metadata.id)) {
                    val pluginId = metadata.id
                    logger.error("Plugin $it got a conflicted plugin id with plugin ${pluginTable[pluginId]?.pluginFilePath}")
                    return@forEach
                }
                pluginInstance.pluginStatus = PluginStatus.UNLOADED
                pluginTable[metadata.id] = pluginInstance
            } catch (e: MultipleCompilationErrorsException) {
                logger.error("An error occurred while loading plugin $it")
                logger.error(e.message)
            } catch (e: Exception) {
                logger.error("An error occurred while loading plugin $it", e)
            }

        }
    }

    fun registerPluginCommand(pluginName: String, command: String) {
        if (pluginCommandTable.containsKey(pluginName)) {
            val list = pluginCommandTable[pluginName]
            list?.add(command)
            pluginCommandTable[pluginName] = list as java.util.ArrayList<String>
            return
        }
        throw PluginNotExistException("The specified plugin $pluginName does not exist.")

    }

    fun loadAll() {
        if (RuntimeConstants.noPlugins) {
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        pluginTable.forEach {
            if (!it.equals("omms-central")) {
                load(it.key)
            }
        }
    }

    fun unloadAll() {
        if (RuntimeConstants.noPlugins) {
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        pluginTable.forEach {
            unload(it.key, true)
        }
    }

    fun getPluginInstance(id: String): GroovyPluginInstance {
        val instance = pluginTable[id] ?: throw PluginNotExistException("Specified plugin $id not exist.")
        if (instance.pluginStatus != PluginStatus.LOADED) {
            throw PluginNotLoadedException(id)
        }
        return instance
    }

    fun execute(
        pluginName: String,
        functionName: String,
        command: Request,
        serverInterface: RequestServerInterface
    ): Any? {
        val pluginInstance = pluginTable[pluginName] ?: throw PluginNotExistException(
            "Plugin $pluginName does not exist."
        )
        if (pluginInstance.pluginStatus == PluginStatus.UNLOADED)
            throw PluginNotLoadedException("Plugin $pluginName hasn't been loaded.")
        return pluginInstance.invokeMethod(functionName, serverInterface, command)

    }

    fun load(pluginName: String) {
        logger.info("Loading Plugin:%s".format(pluginName))
        val pluginInstance = pluginTable[pluginName]
        pluginCommandTable[pluginName] = ArrayList()
        val initServerInterface = LifecycleServerInterface(pluginName)
        if (pluginInstance != null) {
            if (pluginInstance.pluginStatus == PluginStatus.LOADED) {
                throw PluginAlreadyLoadedException("Plugin $pluginName already loaded.")
            }
            try {
                pluginInstance.onLoad(initServerInterface)
                pluginInstance.pluginStatus = PluginStatus.LOADED
            } catch (e: Exception) {
                logger.error("While loading plugin $pluginName ,an error occurred.", e)
            }
        } else {
            throw PluginNotExistException("Plugin $pluginName not exist.")
        }
    }

    fun unload(pluginName: String, ignorePluginStatus: Boolean) {
        logger.info("Unloading Plugin:%s".format(pluginName))
        val pluginInstance = pluginTable[pluginName]
        val lifecycleServerInterface = LifecycleServerInterface(pluginName)
        if (pluginInstance != null) {
            if (!ignorePluginStatus) {
                if (pluginInstance.pluginStatus != PluginStatus.LOADED) {
                    throw PluginNotLoadedException("Plugin $pluginName hasn't been loaded.")
                }
            }
            try {
                pluginInstance.onUnload(lifecycleServerInterface)
                //pluginInstance.invokeMethod("onUnload", initServerInterface)
                RequestManager.unRegisterPluginRequest(pluginName)
                val pluginCommandHahMap = RuntimeConstants.pluginCommandHashMap
                val removed = mutableListOf<PluginCommand>()
                pluginCommandHahMap.forEach {
                    if (it.pluginId == pluginInstance.metadata.id) {
                        removed.add(it)
                    }
                }
                pluginCommandHahMap.removeAll(removed.toSet())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            pluginInstance.pluginStatus = PluginStatus.UNLOADED
        } else {
            throw PluginNotExistException("Plugin $pluginName not exist.")
        }
    }

    fun reload(pluginId: String) {
        if (RuntimeConstants.noPlugins) {
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        unload(pluginId, true)
        init()
        load(pluginId)
    }
}