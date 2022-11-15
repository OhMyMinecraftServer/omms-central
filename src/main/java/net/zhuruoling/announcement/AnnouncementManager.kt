package net.zhuruoling.announcement

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zhuruoling.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import kotlin.io.path.Path


object AnnouncementManager {

    val announcementMap = mutableMapOf<String, Announcement>()
    val gson: Gson = GsonBuilder().serializeNulls().create()
    val logger: Logger = LoggerFactory.getLogger("AM")
    fun init() {
        announcementMap.clear()
        val files = Files.list(Path(Util.joinFilePaths("announcements")))
        val fileList = mutableListOf<String>()
        files.forEach {
            if (it.toFile().extension == "json") {
                fileList.add(it.toFile().absolutePath)
            }
        }
        fileList.forEach {
            try {
                val announcement = gson.fromJson(FileReader(it), Announcement::class.java)
                if (announcementMap.containsKey(announcement.id)){
                    logger.warn("Duplicated id(${announcement.id}) in file $it, ignoring.")
                    return
                }
                announcementMap[announcement.id] = announcement
            }
            catch (e: Throwable){
                logger.error("Cannot read announcement file($it).")
            }
        }
    }

    fun create(announcement: Announcement) {
        try{
            val jsonStr: String = announcement.toJson()
            val path = Path(Util.joinFilePaths("announcements", "${announcement.id.hashCode()}.json"))
            if (Files.exists(path)) {
                throw RuntimeException("Cannot create announcement file.")
            } else {
                Files.createFile(path)
            }
            val fileWriter = FileWriter(path.toFile(), false)
            fileWriter.write(jsonStr)
            fileWriter.flush()
            fileWriter.close()
            logger.info("Successfully created announcement ${announcement.title}, reloading.")
            this.init()
        }
        catch (e: Exception){
            logger.error("Cannot create announcement file.", RuntimeException(e))
        }
    }

    fun remove(id: String) {

    }

    fun get(id: String): Announcement? {
        return announcementMap[id]
    }

    fun searchForTitle(keyWord: String): List<Announcement>? {// TODO: (
        val result = mutableListOf<Announcement>()

        return if (result.isEmpty()) {
            null
        } else {
            result
        }
    }

    fun getLatest(): Announcement? {
        if (announcementMap.size == 1){
            return announcementMap.values.elementAt(0)
        }
        if (announcementMap.isEmpty()){
            return null
        }
        var announcement: Announcement = announcementMap.values.elementAt(0)
        announcementMap.values.forEach{
            if (announcement.timeMillis < it.timeMillis){
                announcement = it
            }
        }
        return announcement
    }
}