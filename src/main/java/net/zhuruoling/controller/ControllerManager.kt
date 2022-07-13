package net.zhuruoling.controller

import net.zhuruoling.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FilenameFilter

object ControllerManager {
    val controllers = mutableListOf<Controller>()
    val logger:Logger = LoggerFactory.getLogger("ControllerManager")
    val socketFile = File(Util.joinFilePaths("con.sock"))
    fun init(){
        val path = File(Util.joinFilePaths("controllers"))
        val files = path.list(FilenameFilter { dir, name -> return@FilenameFilter name.split(".")[name.split(".").size - 1] == "json" })

        if (files != null) {
            if (files.isEmpty()){
                logger.warn("No Controller added to this server.")
                return
            }
        }
        else{
            logger.warn("No Controller added to this server.")
            return
        }

    }

    fun destroy(){

    }
}