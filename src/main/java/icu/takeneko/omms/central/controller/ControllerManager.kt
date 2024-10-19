package icu.takeneko.omms.central.controller

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import icu.takeneko.omms.central.controller.console.ws.packet.PacketTypes
import icu.takeneko.omms.central.plugin.callback.ControllerLoadCallback
import icu.takeneko.omms.central.fundation.Manager
import icu.takeneko.omms.central.util.Util
import org.jetbrains.annotations.NotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileReader
import java.io.FilenameFilter

object ControllerManager : Manager() {
    val controllers = mutableMapOf<String, Controller>()
    val logger: Logger = LoggerFactory.getLogger("ControllerManager")
    val gson: Gson = GsonBuilder().setExclusionStrategies(object : ExclusionStrategy {
        override fun shouldSkipField(p0: FieldAttributes?): Boolean {
            return "controllerHttpClient" in p0!!.name
        }

        override fun shouldSkipClass(p0: Class<*>?): Boolean {
            return p0 == ControllerHttpClient::class.java
        }

    }).serializeNulls().create()

    override fun init() {
        controllers.clear()
        val path = Util.fileOf("controllers")
        val files =
            path.list(FilenameFilter { _, name -> return@FilenameFilter name.split(".")[name.split(".").size - 1] == "json" })
        if (files != null) {
            if (files.isEmpty()) {
                logger.warn("No Controller added to this server.")
                return
            } else {
                files.forEach {
                    logger.debug("controller: $it")
                    val controllerImpl: ControllerImpl =
                        gson.fromJson(FileReader(Util.fileOf("./controllers/", it)), ControllerImpl::class.java)
                    logger.debug(controllerImpl.toString())
                    controllerImpl.fixFields()
                    controllers[controllerImpl.name] = controllerImpl
                }
            }
        } else {
            logger.warn("No Controller added to this server.")
            return
        }
        ControllerLoadCallback.INSTANCE.invokeAll(this)
        PacketTypes.init()
    }

    operator fun plusAssign(controller: Controller) {
        addController(controller)
    }

    fun addController(controller: Controller) {
        if (controller.name in controllers) {
            throw ControllerExistsException(controller.name)
        }
        controllers += controller.name to controller
    }

    fun removeController(controller: Controller) {
        if (controller.name !in controllers) {
            throw ControllerNotExistException(controller.name)
        }
        controllers.remove(controller.name)
    }

    fun replaceController(controller: Controller) {
        controllers[controller.name] = controller
    }

    fun sendCommand(controller: Controller, command: String): CommandExecutionResult {
        return controller.sendCommand(command)
    }

    fun sendCommand(controllerName: String, command: String): CommandExecutionResult {
        if (controllerName in controllers) {
            return this[controllerName]!!.sendCommand(command)
        } else {
            throw ControllerNotExistException(controllerName)
        }
    }

    fun getControllerByName(name: String): Controller? {
        return if (name in controllers) controllers[name] else null
    }

    operator fun contains(name: String): Boolean {
        return name in controllers
    }

    operator fun get(name: String): Controller? {
        return getControllerByName(name)
    }

    @NotNull
    fun getControllerStatus(controllerList: MutableList<String>): MutableMap<String, Status> {
        val map = mutableMapOf<String, Status>()
        controllerList.forEach {
            if (it !in controllers) {
                throw java.lang.IllegalArgumentException("Controller $it not exist")
            } else {
                map[it] = Status()
                map[it]!!.run {
                    name = it
                    type = getControllerByName(it)!!.type
                    isQueryable = getControllerByName(it)!!.isStatusQueryable
                    isAlive = false
                }
            }
        }
        controllerList.forEach {
            if (!this[it]!!.isStatusQueryable) return@forEach
            try {
                // val status = controllerConnector[it]!!.queryStatus()
                val status = this[it]!!.queryControllerStatus()
                map[it]!!.run {
                    this.name = status.name
                    this.isAlive = status.isAlive
                    this.maxPlayerCount = status.maxPlayerCount
                    this.playerCount = status.playerCount
                    this.players = status.players
                }
            } catch (ignored: Exception) {
                logger.warn("Exception occurred while querying status: $ignored")
            }
        }
        return map
    }


}

class ControllerNotExistException(val controllerName: String) : RuntimeException("Controller $controllerName not exist")
class ControllerExistsException(val controllerName: String) :
    RuntimeException("Controller $controllerName already exists.")