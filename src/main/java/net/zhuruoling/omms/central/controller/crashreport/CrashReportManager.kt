package net.zhuruoling.omms.central.controller.crashreport

import cn.hutool.core.io.FileUtil
import net.zhuruoling.omms.central.util.Util
import org.apache.commons.io.FileUtils
import java.nio.file.Files
import kotlin.io.path.Path
//WIP
data class CrashReport(val from:String, val time: Long, val content: List<String>)

object CrashReportManager {
    val storagePath = Path(Util.joinFilePaths("crashReport"))

    fun init(){
        if (!storagePath.toFile().exists()){
            Files.createDirectory(storagePath)
        }
        FileUtil.listFileNames(storagePath.toAbsolutePath().toString()).forEach {

        }

    }

    fun save(from: String, content: List<String>){

    }
}