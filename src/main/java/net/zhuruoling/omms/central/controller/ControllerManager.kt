package net.zhuruoling.omms.central.controller

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.omms.central.network.http.client.ControllerHttpClient
import net.zhuruoling.omms.central.util.Util
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.FilenameFilter

data class CommandOutputData(val controllerId: String, val command: String, val output: String)


object ControllerManager {
    val controllers = mutableMapOf<String, ControllerInstance>()
    private val controllerConnector = mutableMapOf<String, ControllerHttpClient>()
    val logger: Logger = LoggerFactory.getLogger("ControllerManager")
    val gson: Gson = GsonBuilder().serializeNulls().create()

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
                    logger.debug("server:$it")
                    val controller: Controller =
                        gson.fromJson(FileReader(Util.joinFilePaths("./controllers/", it)), Controller().javaClass)
                    logger.debug(controller.toString())
                    try {
                        controllers[controller.name] =
                            ControllerInstance(controller, ControllerUtils.resloveTypeFromString(controller.type))
                        controllerConnector[controller.name] = ControllerHttpClient(controller)
                    } catch (e: IllegalArgumentException) {
                        logger.error(
                            "Cannot resolve controller type symbol: %s".format(controller.type),
                            IllegalControllerTypeException(
                                "Cannot resolve controller type symbol: %s".format(controller.type),
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
    }

    fun sendCommand(controllerName: String, command: String): List<String> {
        if (controllers.containsKey(controllerName)) {
            try{
                return controllerConnector[controllerName]!!.sendCommand(command)
            }catch (e:Exception){
                throw e
            }
        } else {
            throw ControllerNotExistException(controllerName)
        }
    }


    //controller execute survival give @a dirt
    @Nullable
    fun getControllerByName(name: String): ControllerInstance? {
        if (controllers.containsKey(name)) {
            return controllers[name]
        }
        return null
    }

    @NotNull
    fun getControllerStatus(controllerList: MutableList<String>): MutableMap<String, Status> {
        val map = mutableMapOf<String, Status>()
        controllerList.forEach {
            if (!controllers.containsKey(it)) {
                throw java.lang.IllegalArgumentException("Controller not exist")
            } else {
                map[it] = Status()
                map[it]!!.run {
                    name = it
                    type = getControllerByName(it)!!.controllerType
                    isQueryable = getControllerByName(it)!!.controller.isStatusQueryable
                    isAlive = false
                }
            }
        }
        controllerList.forEach {
            if (!getControllerByName(it)!!.controller.isStatusQueryable) return@forEach
            try {
                val status = controllerConnector[it]!!.queryStatus()
                map[it]!!.run {
                    this.name = status.name
                    this.isAlive = true
                    this.isQueryable = true
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

class ControllerNotExistException(controllerName: String) : RuntimeException("Controller $controllerName not exist")
