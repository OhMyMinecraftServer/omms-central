package net.zhuruoling.omms.central.script

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.omms.central.console.PluginCommand
import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.network.session.request.Request
import net.zhuruoling.omms.central.network.session.request.RequestManager
import net.zhuruoling.omms.central.util.Util
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

object ScriptManager {
    val logger: Logger = LoggerFactory.getLogger("PluginManger")
    private var pluginFileList = ArrayList<String>()
    private var pluginTable = HashMap<String, GroovyScriptInstance>()
    val gson: Gson = GsonBuilder().serializeNulls().create()
    private var pluginCommandTable: HashMap<String, ArrayList<String>> = java.util.HashMap()
    fun init() {
        if (GlobalVariable.noPlugins) {
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
                val pluginInstance = GroovyScriptInstance(it)
                pluginInstance.initPlugin()
                val metadata = pluginInstance.metadata
                logger.debug("Metadata of plugin $it is $metadata")
                logger.info("Initiating plugin $it")
                if (pluginTable.contains(metadata.id)) {
                    val pluginId = metadata.id
                    logger.error("Plugin $it got a conflicted plugin id with plugin ${pluginTable[pluginId]?.pluginFilePath}")
                    return@forEach
                }
                pluginInstance.pluginStatus = ScriptStatus.UNLOADED
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
        throw ScriptNotExistException("The specified plugin $pluginName does not exist.")

    }

    fun loadAll() {
        if (GlobalVariable.noPlugins) {
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
        if (GlobalVariable.noPlugins) {
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        pluginTable.forEach {
            unload(it.key, true)
        }
    }

    fun getPluginInstance(id: String): GroovyScriptInstance {
        val instance = pluginTable[id] ?: throw ScriptNotExistException(
            "Specified plugin $id not exist."
        )
        if (instance.pluginStatus != ScriptStatus.LOADED) {
            throw ScriptNotLoadedException(id)
        }
        return instance
    }

    fun execute(
        pluginName: String,
        functionName: String,
        command: Request,
        serverInterface: OperationInterface
    ): Any? {
        val pluginInstance = pluginTable[pluginName] ?: throw ScriptNotExistException(
            "Plugin $pluginName does not exist."
        )
        if (pluginInstance.pluginStatus == ScriptStatus.UNLOADED)
            throw ScriptNotLoadedException("Plugin $pluginName hasn't been loaded.")
        return pluginInstance.invokeMethod(functionName, serverInterface, command)

    }

    fun load(pluginName: String) {
        logger.info("Loading Plugin:%s".format(pluginName))
        val pluginInstance = pluginTable[pluginName]
        pluginCommandTable[pluginName] = ArrayList()
        val initServerInterface =
            LifecycleOperationInterface(pluginName)
        if (pluginInstance != null) {
            if (pluginInstance.pluginStatus == ScriptStatus.LOADED) {
                throw ScriptAlreadyLoadedException("Plugin $pluginName already loaded.")
            }
            try {
                pluginInstance.onLoad(initServerInterface)
                pluginInstance.pluginStatus = ScriptStatus.LOADED
            } catch (e: Exception) {
                logger.error("While loading plugin $pluginName ,an error occurred.", e)
            }
        } else {
            throw ScriptNotExistException("Plugin $pluginName not exist.")
        }
    }

    fun unload(pluginName: String, ignorePluginStatus: Boolean) {
        logger.info("Unloading Plugin:%s".format(pluginName))
        val pluginInstance = pluginTable[pluginName]
        val lifecycleServerInterface =
            LifecycleOperationInterface(pluginName)
        if (pluginInstance != null) {
            if (!ignorePluginStatus) {
                if (pluginInstance.pluginStatus != ScriptStatus.LOADED) {
                    throw ScriptNotLoadedException("Plugin $pluginName hasn't been loaded.")
                }
            }
            try {
                pluginInstance.onUnload(lifecycleServerInterface)
                //pluginInstance.invokeMethod("onUnload", initServerInterface)
                RequestManager.unRegisterPluginRequest(pluginName)

                val pluginCommandHahMap = GlobalVariable.pluginCommandHashMap
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

            pluginInstance.pluginStatus = ScriptStatus.UNLOADED
        } else {
            throw ScriptNotExistException("Plugin $pluginName not exist.")
        }
    }

    fun reload(pluginId: String) {
        if (GlobalVariable.noPlugins) {
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        unload(pluginId, true)
        init()
        load(pluginId)
    }
}