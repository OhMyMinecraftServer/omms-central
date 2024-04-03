package icu.takeneko.omms.central.announcement

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import icu.takeneko.omms.central.util.Manager
import icu.takeneko.omms.central.util.SearchResult
import icu.takeneko.omms.central.util.Util
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import kotlin.io.path.Path


object AnnouncementManager : Manager() {

    val announcementMap = mutableMapOf<String, Announcement>()
    val gson: Gson = GsonBuilder().serializeNulls().create()
    val logger: Logger = LoggerFactory.getLogger("AM")
    override fun init() {
        announcementMap.clear()
        val files = Files.list(Util.absolutePath("announcements"))
        val fileList = mutableListOf<String>()
        files.forEach {
            if (it.toFile().extension == "json") {
                fileList.add(it.toFile().absolutePath)
            }
        }
        fileList.forEach {
            try {
                val announcement = gson.fromJson(FileReader(it), Announcement::class.java)
                if (announcementMap.containsKey(announcement.id)) {
                    logger.warn("Duplicated id(${announcement.id}) in file $it, ignoring.")
                    return
                }
                if (announcement.contentType == null) {
                    announcement.contentType = ContentType.STRING
                }
                announcementMap[announcement.id] = announcement
            } catch (e: Throwable) {
                logger.error("Cannot read announcement file($it).")
            }
        }
    }

    fun create(announcement: Announcement) {
        try {
            val jsonStr: String = announcement.toJson()
            val path = Util.absolutePath("announcements", "${announcement.id.hashCode()}.json")
            if (Files.exists(path)) {
                throw RuntimeException("Cannot create announcement file.")
            } else {
                Files.createFile(path)
            }
            val fileWriter = FileWriter(path.toFile(), false)
            fileWriter.write(jsonStr)
            fileWriter.flush()
            fileWriter.close()
            logger.info("Created announcement ${announcement.title}, reloading.")
            this.init()
        } catch (e: Exception) {
            logger.error("Cannot create announcement file.", RuntimeException(e))
        }
    }

    fun remove(id: String) {

    }

    fun get(id: String): Announcement? {
        return announcementMap[id]
    }

    fun searchForTitle(keyWord: String, relevantThreshold: Int = 70): List<AnnouncementSearchResult> {
        synchronized(this.announcementMap) {
            val result = mutableListOf<AnnouncementSearchResult>()
            val tiMap = this.announcementMap.map { it.key to it.value.title }.toMap()
            tiMap.forEach { (t, u) ->
                val r = FuzzySearch.tokenSortPartialRatio(keyWord, u)
                if (r > relevantThreshold) {
                    result += AnnouncementSearchResult(announcementMap[t]!!, r)
                }
            }
            result.sort()
            return result
        }
    }

    fun getLatest(): Announcement? {
        if (announcementMap.size == 1) {
            return announcementMap.values.elementAt(0)
        }
        if (announcementMap.isEmpty()) {
            return null
        }
        var announcement: Announcement = announcementMap.values.elementAt(0)
        announcementMap.values.forEach {
            if (announcement.timeMillis < it.timeMillis) {
                announcement = it
            }
        }
        return announcement
    }
}


class AnnouncementSearchResult(announcement: Announcement, ratio: Int) : SearchResult<Announcement>(announcement, ratio)