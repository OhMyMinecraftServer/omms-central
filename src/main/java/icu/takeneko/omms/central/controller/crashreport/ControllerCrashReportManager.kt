package icu.takeneko.omms.central.controller.crashreport

import icu.takeneko.omms.central.controller.ControllerManager
import icu.takeneko.omms.central.plugin.callback.RecievedControllerCrashReportCallback
import icu.takeneko.omms.central.foundation.Manager
import icu.takeneko.omms.central.util.Util
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.slf4j.LoggerFactory
import java.nio.file.Files
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries

object ControllerCrashReportManager : Manager() {
    private val storagePath = Util.absolutePath("crashReport")
    private val logger = LoggerFactory.getLogger("ControllerCrashReportManager")
    private val crashReports = mutableListOf<CrashReportStorage>()
    private val json = Json{
        encodeDefaults = true
        prettyPrint = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun init() {
        crashReports.clear()
        if (!storagePath.toFile().exists()) {
            Files.createDirectory(storagePath)
        }
        storagePath.toAbsolutePath().listDirectoryEntries().forEach {

            it.inputStream().use { i ->
                try {
                    crashReports += json.decodeFromStream<CrashReportStorage>(i)
                }catch (e:Exception){
                    logger.error("Failed to load crashReport: $it")
                }
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
            it.write(json.encodeToString(crashReport))
            it.flush()
        }
        crashReports += crashReport
    }

    fun getLatest(controllerId: String): CrashReportStorage? {
        val collection = crashReports
            .filter { it.controllerId == controllerId }
        if (collection.isEmpty())return null
        return collection.maxByOrNull { it.timeMillis }
    }
}