package icu.takeneko.omms.central.whitelist

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import icu.takeneko.omms.central.plugin.callback.WhitelistLoadCallback
import icu.takeneko.omms.central.util.Manager
import icu.takeneko.omms.central.util.Util
import icu.takeneko.omms.central.whitelist.builtin.BuiltinWhitelist
import icu.takeneko.omms.central.whitelist.builtin.BuiltinWhitelistData
import icu.takeneko.omms.central.whitelist.builtin.WhitelistAlias
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.inputStream

@SuppressWarnings("all")
object WhitelistManager : Manager() {

    private val whitelistMap = HashMap<String, Whitelist>()
    private val gson = GsonBuilder().serializeNulls().create()
    private val logger = LoggerFactory.getLogger("WhitelistManager")

    @OptIn(ExperimentalSerializationApi::class)
    override fun init() {
        whitelistMap.clear()
        val folder = Util.fileOf("whitelists")
        val files = mutableListOf<Path>()
        Files.list(folder.toPath()).forEach {
            if (!it.toFile().isFile) return@forEach
            val file = it.toFile()
            if (file.extension == "json") {
                files.add(it)
            }
        }
        files.forEach {
            try {
                val instance = it.inputStream().use { BuiltinWhitelist.json.decodeFromStream<BuiltinWhitelistData>(it) }
                if (it.toFile().name != "${instance.name}.json") {
                    logger.warn("Whitelist name(${instance.name}) does not match with file name(${it.toFile().name}).")
                    logger.warn("Renaming ${it.toFile().name} -> ${instance.name}.json")
                    whitelistNameFix(it)
                }
                if (whitelistMap.containsKey(instance.name)) {
                    throw RuntimeException("Duplicated whitelist name(${instance.name}).")
                }
                whitelistMap[instance.name] = BuiltinWhitelist(data = instance)
            } catch (e: JsonParseException) {
                throw e
            } catch (e: Exception) {
                throw IOException("Cannot load whitelist file(${it.toFile().absolutePath}).", e)
            }
        }
        whitelistMap.values.forEach {
            it.saveModifiedBuffer()
        }
        val add = mutableMapOf<String, Whitelist>()
        for (value in whitelistMap.values) {
            if (
                if (value is ProxyableWhitelist) {
                    var notConflicts = false
                    value.aliases.forEach {
                        if (it == value.name) throw RuntimeException("A whitelist cannot has a alias has the same name with itself")
                        if (it in whitelistMap) throw RuntimeException("Duplicated whitelist name($it).")
                        add[it] = WhitelistAlias(value, it).also { i -> value.onDelegateCreate(i) }
                        notConflicts = true
                    }
                    notConflicts
                } else {
                    false
                }
            ) {
                logger.info("Setting up whitelist proxies for ${value.name}")
            }
        }
        whitelistMap += add
        WhitelistLoadCallback.INSTANCE.invokeAll(this)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun whitelistNameFix(filePath: Path) {
        val data = FileInputStream(filePath.toFile()).use {
            BuiltinWhitelist.json.decodeFromStream<BuiltinWhitelistData>(it)
        }
        val newFileName = data.name + ".json"
        FileUtils.moveFile(filePath.toFile(), Util.fileOf("whitelists", newFileName))
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
    fun flush(whitelistName: String) {
        (this[whitelistName] ?: throw WhitelistNotExistException(whitelistName)).saveModifiedBuffer()
    }

    @Throws(WhitelistAlreadyExistsException::class)
    fun createWhitelist(name: String) {
        if (name in whitelistMap) throw WhitelistAlreadyExistsException(name)
        val data = BuiltinWhitelistData(name)
        val wl = BuiltinWhitelist(data)
        whitelistMap += name to BuiltinWhitelist(data)
        wl.saveModifiedBuffer()
    }

    @Throws(WhitelistNotExistException::class)
    fun deleteWhiteList(name: String) {
        val whitelist = this[name] ?: throw WhitelistNotExistException(name)
        whitelist.deleteWhitelist()
        if (whitelist is ProxyableWhitelist) {
            whitelist.aliases.forEach{
                whitelistMap -= it
            }
        }
        this.whitelistMap.remove(name)
    }

    data class SearchResult(val ratio: Int, val playerName: String)

}