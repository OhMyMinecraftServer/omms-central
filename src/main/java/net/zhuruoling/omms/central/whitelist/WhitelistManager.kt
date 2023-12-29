package net.zhuruoling.omms.central.whitelist

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import me.xdrop.fuzzywuzzy.FuzzySearch
import net.zhuruoling.omms.central.plugin.callback.WhitelistLoadCallback
import net.zhuruoling.omms.central.util.Manager
import net.zhuruoling.omms.central.util.Util
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.jvm.Throws

@SuppressWarnings("all")
object WhitelistManager : Manager() {

    private val whitelistMap = HashMap<String, Whitelist>()
    private val gson = GsonBuilder().serializeNulls().create()
    private val logger = LoggerFactory.getLogger("WhitelistManager")
    override fun init() {
        whitelistMap.clear()
        val folder = File(Util.joinFilePaths("whitelists"))
        val files = mutableListOf<Path>()
        val gson = GsonBuilder().serializeNulls().create()
        Files.list(folder.toPath()).forEach {
            if (!it.toFile().isFile) return@forEach
            val file = it.toFile()
            if (file.extension == "json") {
                files.add(it)
            }
        }
        files.forEach {
            try {
                val reader = FileReader(it.toFile())
                val whitelistImpl = gson.fromJson(reader, WhitelistImpl::class.java)
                if (it.toFile().name != "${whitelistImpl.name}.json") {
                    logger.warn("Whitelist name(${whitelistImpl.name}) does not match with file name(${it.toFile().name}).")
                    logger.warn("Renaming ${it.toFile().name} -> ${whitelistImpl.name}.json")
                    reader.close()
                    whitelistNameFix(it)
                }
                if (whitelistMap.containsKey(whitelistImpl.name)) {
                    throw RuntimeException("Duplicated whitelist name(${whitelistImpl.name}).")
                }
                whitelistMap[whitelistImpl.name] = whitelistImpl
                reader.close()
            } catch (e: JsonParseException) {
                throw e
            } catch (e: Exception) {
                throw IOException("Cannot load whitelist file(${it.toFile().absolutePath}).", e)
            }
        }
        WhitelistLoadCallback.INSTANCE.invokeAll(this)
    }

    private fun whitelistNameFix(filePath: Path) {
        val reader = FileReader(filePath.toFile())
        val whitelistImpl = gson.fromJson(reader, WhitelistImpl::class.javaObjectType)
        reader.close()
        val newFileName = whitelistImpl.name + ".json"
        FileUtils.moveFile(filePath.toFile(), File(Util.joinFilePaths("whitelists", newFileName)))
    }

    fun addWhitelist(whitelist: Whitelist) {
        if (whitelist.name in this.whitelistMap) throw WhitelistAlreadyExistsException(whitelist.name)
        whitelistMap += whitelist.name to whitelist
    }

    operator fun plusAssign(whitelist: Whitelist) {
        addWhitelist(whitelist)
    }

    fun queryWhitelist(whitelistName: String, value: String): Boolean {
        val whitelist = whitelistMap[whitelistName] ?: throw WhitelistNotExistException(whitelistName)
        return value in whitelist
    }

    fun queryInAllWhitelist(player: String): MutableList<String> {
        val list = mutableListOf<String>()
        whitelistMap.forEach {
            if (player in it.value) {
                list.add(it.key)
            }
        }
        return list
    }

    fun getAllWhitelist() = whitelistMap

    fun getWhitelist(whitelistName: String): Whitelist? {
        return whitelistMap[whitelistName]
    }

    fun getWhitelists(): MutableCollection<Whitelist> {
        return whitelistMap.values
    }

    fun isNoWhitelist(): Boolean {
        return whitelistMap.isEmpty()
    }

    fun hasWhitelist(whitelistName: String): Boolean {
        return whitelistMap.containsKey(whitelistName)
    }

    fun forEach(action: (Map.Entry<String, Whitelist>) -> Unit) {
        whitelistMap.forEach {
            action(it)
        }
    }

    @Synchronized
    fun searchInWhitelist(whitelistName: String, playerName: String): List<SearchResult> {
        val whitelist =
            whitelistMap[whitelistName] ?: throw WhitelistNotExistException("Whitelist $whitelistName not found.")
        val result = mutableListOf<SearchResult>()
        whitelist.players.forEach {
            val ratio = FuzzySearch.tokenSortPartialRatio(it, playerName)
            if (ratio >= 70) {
                result.add(SearchResult(ratio, it))
            }
        }
        result.sortBy {
            it.ratio
        }
        return result
    }

    fun getWhitelistNames(): MutableSet<String> {
        return whitelistMap.keys
    }


    @Synchronized
    @Throws(WhitelistNotExistException::class, PlayerAlreadyExistsException::class)
    fun addToWhiteList(whitelistName: String, value: String, flush: Boolean = true) {
        val whitelist = this[whitelistName] ?: throw WhitelistNotExistException(whitelistName)
        synchronized(whitelist) {
            whitelist.addPlayer(value)
        }
        if (flush) whitelist.saveModifiedBuffer()
    }

    private operator fun get(whitelistName: String): Whitelist? {
        return this.whitelistMap[whitelistName]
    }

    @Synchronized
    @Throws(WhitelistNotExistException::class, PlayerNotFoundException::class)
    fun removeFromWhiteList(whitelistName: String, value: String) {
        val whitelist = this[whitelistName] ?: throw WhitelistNotExistException(whitelistName)
        synchronized(whitelist) {
            whitelist.removePlayer(value)
        }
        whitelist.saveModifiedBuffer()
    }

    @Synchronized
    fun flush(whitelistName: String){
        (this[whitelistName] ?: throw WhitelistNotExistException(whitelistName)).saveModifiedBuffer()
    }

    @Throws(WhitelistAlreadyExistsException::class)
    fun createWhitelist(name: String) {
        if (name in whitelistMap) throw WhitelistAlreadyExistsException(name)
        val whitelist = WhitelistImpl(mutableListOf(), name)
        whitelistMap += name to whitelist
        whitelist.saveModifiedBuffer()
    }

    @Throws(WhitelistNotExistException::class)
    fun deleteWhiteList(name: String) {
        val whitelist = this[name] ?: throw WhitelistNotExistException(name)
        whitelist.deleteWhitelist()
        this.whitelistMap.remove(name)
    }

    data class SearchResult(val ratio: Int, val playerName: String)

}