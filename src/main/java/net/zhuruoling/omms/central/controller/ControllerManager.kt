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
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object ControllerManager {
    val controllers = mutableMapOf<String, ControllerInstance>()
    val logger: Logger = LoggerFactory.getLogger("ControllerManager")
    val gson: Gson = GsonBuilder().serializeNulls().create()
    private val statusCache = mutableMapOf<String, Status>()

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

    fun sendInstruction(controllerName: String, command: String) {
        getControllerByName(controllerName)?.let { this.sendInstruction(it, command) }
    }

    fun sendInstruction(instance: ControllerInstance, command: String) {
        val instruction = Instruction(instance.controllerType, instance.controller.name, command)
        RuntimeConstants.udpBroadcastSender?.addToQueue(Util.TARGET_CONTROL, Instruction.asJsonString(instruction))
    }

    private fun sendInstruction(command: Instruction) {
        RuntimeConstants.udpBroadcastSender?.addToQueue(Util.TARGET_CONTROL, Instruction.asJsonString(command))
    }

    fun putStatusCache(status: Status) {
        synchronized(statusCache) {
            this.statusCache[status.name] = status
        }
    }

    private fun clearStatusCache() {
        synchronized(statusCache){
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
        synchronized(statusCache){
            val map = mutableMapOf<String, Status>()
            println("Fetching controller statuses.")
            val target = Util.generateRandomTarget()
            clearStatusCache()
            controllerList.forEach {
                if (!controllers.containsKey(it)) {
                    throw java.lang.IllegalArgumentException("Controller not exist")
                }
            }
            for (s in controllerList) {
                val c = controllers[s]!!
                if (c.controller.isStatusQueryable) {
                    this.sendInstruction(
                        Instruction(
                            c.controllerType,
                            c.controller.name,
                            Util.toJson(target),
                            InstructionType.UPLOAD_STATUS
                        )
                    )
                }
            }
            val task = FutureTask {
                var end = false
                while (!end){
                    var canEnd = true
                    statusCache.forEach {
                        if (!controllerList.contains(it.key)){
                            canEnd = false
                        }
                    }
                    end = canEnd
                }
                return@FutureTask
            }
            task.run()
            try {
                var result = task[5000, TimeUnit.MILLISECONDS]
            }catch (ignored: TimeoutException){ }
            controllerList.forEach {
                if (statusCache.containsKey(it)){
                    map[it] = statusCache[it]!!
                }else{
                    val status = Status()
                    status.setQueryable(this.controllers[it]!!.controller.isStatusQueryable)
                    map[it] = status
                }
            }
            return map
        }
    }


}
