package net.zhuruoling.controller

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.main.RuntimeConstants
import net.zhuruoling.util.Util
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.FilenameFilter

object ControllerManager {
    val controllers = mutableMapOf<String, ControllerInstance>()
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
        logger.info(controllers.toString())
    }

    fun sendInstruction(controllerName: String, command: String) {
        getControllerByName(controllerName)?.let { this.sendInstruction(it, command) }
    }

    fun sendInstruction(instance: ControllerInstance, command: String) {
        val instruction = Instruction(instance.controllerType, instance.controller.name, command)
        logger.info(Instruction.asJsonString(instruction))
        RuntimeConstants.udpBroadcastSender?.addToQueue(Util.TARGET_CONTROL, Instruction.asJsonString(instruction))
    }

    //controller execute survival give @a dirt
    @Nullable
    fun getControllerByName(name: String): ControllerInstance? {
        if (controllers.containsKey(name)) {
            return controllers[name]
        }
        return null
    }
}