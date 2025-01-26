package icu.takeneko.omms.central.controller

import icu.takeneko.omms.central.foundation.Manager
import icu.takeneko.omms.central.plugin.callback.ControllerLoadCallback
import icu.takeneko.omms.central.util.Util
import icu.takeneko.omms.central.util.plusAssign
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jetbrains.annotations.NotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ControllerManager : Manager() {
    val controllers = mutableMapOf<String, Controller>()
    val logger: Logger = LoggerFactory.getLogger("ControllerManager")
    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun init() {
        controllers.clear()
        val path = Util.fileOf("controllers")
        controllers += path.listFiles { _, name -> name.endsWith(".json") }
            ?.map {
                it.inputStream().use { ins ->
                    json.decodeFromStream<ControllerData>(ins)
                }.inflate()
            } ?: listOf()
        ControllerLoadCallback.INSTANCE.invokeAll(this)
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