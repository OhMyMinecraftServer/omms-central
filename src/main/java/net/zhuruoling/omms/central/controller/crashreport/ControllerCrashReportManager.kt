package net.zhuruoling.omms.central.controller.crashreport

import cn.hutool.core.io.FileUtil
import net.zhuruoling.omms.central.util.Manager
import net.zhuruoling.omms.central.util.Util
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.util.logging.Logger
import kotlin.io.path.Path
//WIP

object ControllerCrashReportManager : Manager() {
    private val storagePath = Path(Util.joinFilePaths("crashReport"))
    private val logger = LoggerFactory.getLogger("ControllerCrashReportManager")
    override fun init(){
        if (!storagePath.toFile().exists()){
            Files.createDirectory(storagePath)
        }
        FileUtil.listFileNames(storagePath.toAbsolutePath().toString()).forEach {
            File(it).reader().use {

            }
        }

    }

    fun save(crashReport: CrashReportStorage){
        logger.debug("controller : ${crashReport.controllerId}")
        logger.debug("content:")
        crashReport.content.forEach(logger::debug)

    }
}