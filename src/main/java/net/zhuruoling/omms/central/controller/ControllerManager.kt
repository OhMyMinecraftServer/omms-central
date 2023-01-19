package net.zhuruoling.omms.central.controller

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.omms.central.main.RuntimeConstants
import net.zhuruoling.omms.central.util.Util
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.FilenameFilter
import java.util.concurrent.FutureTask

data class CommandOutputData(val controllerId:String, val command:String, val output:String)


object ControllerManager {
    val controllers = mutableMapOf<String, ControllerInstance>()
    val logger: Logger = LoggerFactory.getLogger("ControllerManager")
    val gson: Gson = GsonBuilder().serializeNulls().create()
    private val statusCache = mutableMapOf<String, Status>()
    private val commandOutputCache = mutableMapOf<String, MutableMap<String, String>>()

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
                        commandOutputCache[controller.name] = mutableMapOf()
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

    fun sendCommand(controllerName: String, command: String) {
        getControllerByName(controllerName)?.let { this.sendCommand(it, command) }
    }

    fun sendCommand(instance: ControllerInstance, command: String):String? {
        val instruction = Instruction(instance.controllerType, instance.controller.name, command)
        instruction.setType(InstructionType.RUN_COMMAND)
        logger.info("Sending command $command to controller ${instance.controller.name}")
        val json = Instruction.asJsonString(instruction)
        RuntimeConstants.udpBroadcastSender?.addToQueue(Util.TARGET_CONTROL, json)
        commandOutputCache[instance.controller.name] = mutableMapOf()
        var countdown = 500
        while (countdown >= 0){
            if(commandOutputCache[instance.controller.name]!!.containsKey(command))
                break
            Thread.sleep(10)
            countdown--
        }
        return commandOutputCache[instance.controller.name]!![command]
    }

    private fun sendCommand(command: Instruction) {
        RuntimeConstants.udpBroadcastSender?.addToQueue(Util.TARGET_CONTROL, Instruction.asJsonString(command))
    }

    fun putStatusCache(status: Status) {

            this.statusCache[status.name] = status

    }

    private fun clearStatusCache() {
        synchronized(statusCache) {
            statusCache.clear()
        }
    }

    fun getStatusCache() = statusCache


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
        println("Fetching controller statuses.")
        val target = Util.generateRandomTarget()
        clearStatusCache()
        controllerList.forEach {
            if (!controllers.containsKey(it)) {
                throw java.lang.IllegalArgumentException("Controller not exist")
            }
        }
        controllerList.forEach {
            val c = controllers[it]!!
            if (c.controller.isStatusQueryable) {
                this.sendCommand(
                    Instruction(
                        c.controllerType,
                        c.controller.name,
                        Util.toJson(target),
                        InstructionType.UPLOAD_STATUS
                    )
                )
            }
        }
        var countdown = 500
        val task = FutureTask {
            while (true) {
                Thread.sleep(10)
                countdown--
                var allContains = true
                controllerList.forEach {
                    if (!statusCache.containsKey(it)) {
                        allContains = false
                    }
                }
                if (allContains || countdown <= 1) {
                    break
                }
            }
            return@FutureTask statusCache
        }
        task.run()

        controllerList.forEach {
            if (statusCache.containsKey(it)) {
                map[it] = statusCache[it]!!
            } else {
                val status = Status()
                status.setName(it)
                status.setQueryable(this.controllers[it]!!.controller.isStatusQueryable)
                map[it] = status
            }
        }
        return map
    }

    fun putCommandCache(commandOutputData: CommandOutputData) {
        commandOutputCache[commandOutputData.controllerId]!![commandOutputData.command] = commandOutputData.output
    }


}
