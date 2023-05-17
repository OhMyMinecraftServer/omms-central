@file:SuppressWarnings("all")
@file:Suppress("all")

package net.zhuruoling.omms.central.script


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.omms.central.GlobalVariable
import net.zhuruoling.omms.central.network.session.request.Request
import net.zhuruoling.omms.central.network.session.request.RequestManager
import net.zhuruoling.omms.central.util.Manager
import net.zhuruoling.omms.central.util.Util
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

object ScriptManager : Manager() {
    val logger: Logger = LoggerFactory.getLogger("ScriptManger")
    private var scriptFileList = ArrayList<String>()
    private var scriptInstanceHashMap = HashMap<String, GroovyScriptInstance>()
    val gson: Gson = GsonBuilder().serializeNulls().create()
    private var scriptCommandTable: HashMap<String, ArrayList<String>> = java.util.HashMap()
    override fun init() {
        if (GlobalVariable.noScripts) {
            logger.warn("--noscript has been set, ${Util.PRODUCT_NAME} won`t load any script")
            return
        }
        scriptInstanceHashMap.clear()
        scriptFileList.clear()
        val files = Files.list(Path.of(Util.joinFilePaths("scripts")))
        files.forEach {
            if (it.toFile().extension == "groovy") {
                scriptFileList.add(it.toFile().absolutePath)
            }
        }
        logger.debug(scriptFileList.toString())
        scriptFileList.forEach {
            try {
                val pluginInstance = GroovyScriptInstance(it)
                pluginInstance.initPlugin()
                val metadata = pluginInstance.metadata
                logger.debug("Metadata of script {} is {}", it, metadata)
                logger.info("Initiating script $it")
                if (scriptInstanceHashMap.contains(metadata.id)) {
                    val pluginId = metadata.id
                    logger.error("Script $it got a conflicted script id with script ${scriptInstanceHashMap[pluginId]?.pluginFilePath}")
                    return@forEach
                }
                pluginInstance.pluginStatus = ScriptStatus.UNLOADED
                scriptInstanceHashMap[metadata.id] = pluginInstance
            } catch (e: MultipleCompilationErrorsException) {
                logger.error("An error occurred while loading script $it")
                logger.error(e.message)
            } catch (e: Exception) {
                logger.error("An error occurred while loading script $it", e)
            }

        }
    }


    fun registerPluginCommand(scriptName: String, command: String) {
        if (scriptCommandTable.containsKey(scriptName)) {
            val list = scriptCommandTable[scriptName]
            list?.add(command)
            scriptCommandTable[scriptName] = list as java.util.ArrayList<String>
            return
        }
        throw ScriptNotExistException("The specified script $scriptName does not exist.")

    }

    fun loadAll() {
        if (GlobalVariable.noPlugins) {
            logger.warn("--noplugins has been set, ${Util.PRODUCT_NAME} won`t load any plugins")
            return
        }
        scriptInstanceHashMap.forEach {
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
        scriptInstanceHashMap.forEach {
            unload(it.key, true)
        }
    }

    fun getPluginInstance(id: String): GroovyScriptInstance {
        val instance = scriptInstanceHashMap[id] ?: throw ScriptNotExistException(
            "Specified script $id not exist."
        )
        if (instance.pluginStatus != ScriptStatus.LOADED) {
            throw ScriptNotLoadedException(id)
        }
        return instance
    }

    fun execute(
        scriptName: String,
        functionName: String,
        command: Request,
        serverInterface: OperationInterface
    ): Any? {
        val pluginInstance = scriptInstanceHashMap[scriptName] ?: throw ScriptNotExistException(
            "Script $scriptName does not exist."
        )
        if (pluginInstance.pluginStatus == ScriptStatus.UNLOADED)
            throw ScriptNotLoadedException("Script $scriptName hasn't been loaded.")
        return pluginInstance.invokeMethod(functionName, serverInterface, command)

    }

    fun load(scriptName: String) {
        logger.info("Loading Plugin:%s".format(scriptName))
        val pluginInstance = scriptInstanceHashMap[scriptName]
        scriptCommandTable[scriptName] = ArrayList()
        val initServerInterface =
            LifecycleOperationInterface(scriptName)
        if (pluginInstance != null) {
            if (pluginInstance.pluginStatus == ScriptStatus.LOADED) {
                throw ScriptAlreadyLoadedException("Script $scriptName already loaded.")
            }
            try {
                pluginInstance.onLoad(initServerInterface)
                pluginInstance.pluginStatus = ScriptStatus.LOADED
            } catch (e: Exception) {
                logger.error("While loading script $scriptName ,an error occurred.", e)
            }
        } else {
            throw ScriptNotExistException("Script $scriptName not exist.")
        }
    }

    fun unload(scriptName: String, ignoreScriptStatus: Boolean) {
        logger.info("Unloading Plugin:%s".format(scriptName))
        val scriptInstance = scriptInstanceHashMap[scriptName]
        val lifecycleServerInterface =
            LifecycleOperationInterface(scriptName)
        if (scriptInstance != null) {
            if (!ignoreScriptStatus) {
                if (scriptInstance.pluginStatus != ScriptStatus.LOADED) {
                    throw ScriptNotLoadedException("Script $scriptName hasn't been loaded.")
                }
            }
            try {
                scriptInstance.onUnload(lifecycleServerInterface)
                RequestManager.unRegisterPluginRequest(scriptName)

                val pluginCommandHahMap = GlobalVariable.pluginCommandHashMap
                val removed = pluginCommandHahMap.stream().filter { it.pluginId == scriptInstance.metadata.id }.toList()
                pluginCommandHahMap.removeAll(removed.toSet())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            scriptInstance.pluginStatus = ScriptStatus.UNLOADED
        } else {
            throw ScriptNotExistException("Script $scriptName not exist.")
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