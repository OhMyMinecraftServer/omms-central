package net.zhuruoling.omms.central.controller

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.omms.central.network.http.client.ControllerHttpClient
import net.zhuruoling.omms.central.plugin.callback.ControllerLoadCallback
import net.zhuruoling.omms.central.util.Util
import org.jetbrains.annotations.NotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.FilenameFilter

data class CommandOutputData(val controllerId: String, val command: String, val output: String)


object ControllerManager {
    val controllers = mutableMapOf<String, Controller>()
    private val controllerConnector = mutableMapOf<String, ControllerHttpClient>()
    val logger: Logger = LoggerFactory.getLogger("ControllerManager")
    val gson: Gson = GsonBuilder().setExclusionStrategies(object : ExclusionStrategy {
        override fun shouldSkipField(p0: FieldAttributes?): Boolean {
            return "controllerHttpClient" in p0!!.name
        }

        override fun shouldSkipClass(p0: Class<*>?): Boolean {
            return p0 == ControllerHttpClient::class.java
        }

    }).serializeNulls().create()

    fun init() {
        controllers.clear()
        val path = File(Util.joinFilePaths("controllers"))
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
                        gson.fromJson(FileReader(Util.joinFilePaths("./controllers/", it)), ControllerImpl::class.java)
                    logger.debug(controllerImpl.toString())
                    try {
                        controllerImpl.fixFields()
                        controllers[controllerImpl.name] = controllerImpl
                        controllerConnector[controllerImpl.name] = ControllerHttpClient(controllerImpl)
                    } catch (e: IllegalArgumentException) {
                        logger.error(
                            "Cannot resolve controller type symbol: %s".format(controllerImpl.type),
                            IllegalControllerTypeException(
                                "Cannot resolve controller type symbol: %s".format(controllerImpl.type),
                                e
                            )
                        )
                    }
                }
            }
        } else {
            logger.warn("No Controller added to this server.")
            return
        }
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

    fun sendCommand(controllerName: String, command: String): List<String> {
        if (controllerName in controllers) {
            try {
                return this[controllerName]!!.sendCommand(command)
            } catch (e: Exception) {
                throw e
            }
        } else {
            throw ControllerNotExistException(controllerName)
        }
    }

    //controller execute survival give @a dirt
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
                throw java.lang.IllegalArgumentException("Controller not exist")
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
                    this.isAlive = true
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