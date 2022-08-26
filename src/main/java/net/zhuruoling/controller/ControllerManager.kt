package net.zhuruoling.controller

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.main.RuntimeConstants
import net.zhuruoling.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.FilenameFilter

object ControllerManager {
    private val controllers = mutableMapOf<String,Controller>()
    val logger:Logger = LoggerFactory.getLogger("ControllerManager")
    val gson: Gson = GsonBuilder().serializeNulls().create()

    fun init(){
        val path = File(Util.joinFilePaths("controllers"))
        val files = path.list(FilenameFilter { _, name -> return@FilenameFilter name.split(".")[name.split(".").size - 1] == "json" })
        if (files != null) {
            if (files.isEmpty()){
                logger.warn("No Controller added to this server.")
                return
            }
            else{
                files.forEach {
                    logger.debug("server:$it")
                    val controller: Controller = gson.fromJson(FileReader(Util.joinFilePaths("./controllers/",it)), Controller().javaClass)
                    logger.debug(controller.toString())
                    controllers[controller.name] = controller
                }
                logger.debug(controllers.toString())
            }
        }
        else{
            logger.warn("No Controller added to this server.")
            return
        }
    }

    fun sendInstruction(controllerName: String, command: String){
        getControllerByName(controllerName)?.let { this.sendInstruction(it, command) }
    }

    fun sendInstruction(instance: Controller, command: String){
        val instruction = Instruction(ControllerUtils.resloveTypeFromString(instance.type),instance.name, command)
        RuntimeConstants.udpBroadcastSender?.addToQueue(Util.TARGET_CONTROL, Instruction.asJsonString(instruction))
    }

    fun getControllerByName(name: String): Controller?{
        return controllers[name]
    }
}