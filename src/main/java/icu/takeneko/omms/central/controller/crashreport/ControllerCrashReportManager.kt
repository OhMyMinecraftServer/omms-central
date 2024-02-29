package icu.takeneko.omms.central.controller.crashreport

import icu.takeneko.omms.central.controller.ControllerManager
import icu.takeneko.omms.central.plugin.callback.RecievedControllerCrashReportCallback
import icu.takeneko.omms.central.util.Manager
import icu.takeneko.omms.central.util.Util
import org.slf4j.LoggerFactory
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.reader

object ControllerCrashReportManager : Manager() {
    private val storagePath = Path(Util.joinFilePaths("crashReport"))
    private val logger = LoggerFactory.getLogger("ControllerCrashReportManager")
    private val crashReports = mutableListOf<CrashReportStorage>()

    override fun init() {
        crashReports.clear()
        if (!storagePath.toFile().exists()) {
            Files.createDirectory(storagePath)
        }
        storagePath.toAbsolutePath().listDirectoryEntries().forEach {
            it.reader().use {reader ->
                crashReports += Util.gson.fromJson(reader, CrashReportStorage::class.java)
            }
        }
    }

    fun createNewCrashReport(controller: String, content: String) {
        val crashReport = ControllerManager.controllers[controller]!!.convertCrashReport(content)
        logger.debug("controller : ${crashReport.controllerId}")
        logger.debug("content: ")
        crashReport.content.forEach(logger::debug)
        RecievedControllerCrashReportCallback.INSTANCE.invokeAll(crashReport)
        val fileName = storagePath.resolve(Util.generateRandomString(16) + ".json")
        fileName.toFile().writer().use {
            Util.gson.toJson(crashReport, it)
        }
        crashReports += crashReport
    }
}