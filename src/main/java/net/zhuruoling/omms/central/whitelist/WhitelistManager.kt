package net.zhuruoling.omms.central.whitelist

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import me.xdrop.fuzzywuzzy.FuzzySearch
import net.zhuruoling.omms.central.network.session.response.Result
import net.zhuruoling.omms.central.util.Util
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.jvm.Throws

@SuppressWarnings("all")
object WhitelistManager {

    private val whitelistMap = HashMap<String, Whitelist>()
    private val gson = GsonBuilder().serializeNulls().create()
    private val logger = LoggerFactory.getLogger("WhitelistManager")
    fun init() {
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
                if (whitelistMap.containsKey(whitelistImpl.getName())) {
                    throw RuntimeException("Duplicated whitelist name(${whitelistImpl.name}).")
                }
                whitelistMap[whitelistImpl.getName()] = whitelistImpl
                reader.close()
            } catch (e: JsonParseException) {
                throw e
            } catch (e: Exception) {
                throw IOException("Cannot load whitelist file(${it.toFile().absolutePath}).", e)
            }
        }
    }

    private fun whitelistNameFix(filePath: Path) {
        val reader = FileReader(filePath.toFile())
        val whitelistImpl = gson.fromJson(reader, WhitelistImpl::class.javaObjectType)
        reader.close()
        val newFileName = whitelistImpl.name + ".json"
        FileUtils.moveFile(filePath.toFile(), File(Util.joinFilePaths("whitelists", newFileName)))
    }

    operator fun plusAssign(whitelist: Whitelist) {
        if (whitelist.name in this.whitelistMap) throw WhitelistAlreadyExistsException(whitelist.name)
        whitelistMap += whitelist.name to whitelist
    }

    fun queryWhitelist(whitelistName: String?, value: String): Result {
        val whitelist = whitelistMap[whitelistName] ?: return Result.WHITELIST_NOT_EXIST
        if (value in whitelist) {
            return Result.OK
        }
        return Result.PLAYER_NOT_EXIST
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
    fun searchInWhitelist(whitelistName: String, playerName: String): List<SearchResult>? {
        val whitelist = whitelistMap[whitelistName] ?: return null
        val result = mutableListOf<SearchResult>()
        whitelist.players.forEach {
            val ratio = FuzzySearch.tokenSortPartialRatio(it, playerName)
            if (ratio >= 70) {
                result.add(SearchResult(ratio, it))
            }
        }
        if (result.isEmpty()) {
            return null
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
    fun addToWhiteList(whitelistName: String, value: String) {
        val whitelist = this[whitelistName] ?: throw WhitelistNotExistException(whitelistName)
        synchronized(whitelist){
            whitelist.addPlayer(value)
        }
    }

    private operator fun get(whitelistName: String): Whitelist? {
        return this.whitelistMap[whitelistName]
    }

    @Synchronized
    @Throws(WhitelistNotExistException::class, PlayerNotFoundException::class)
    fun removeFromWhiteList(whitelistName: String, value: String) {
        val whitelist = this[whitelistName] ?: throw WhitelistNotExistException(whitelistName)
        synchronized(whitelist){
            whitelist.addPlayer(value)
        }
    }

    private fun performWhitelistModify(whitelistName: String, value: String, operation: Operation): Result {
        val whitelist = whitelistMap[whitelistName] ?: return Result.WHITELIST_NOT_EXIST
        val players = mutableListOf<String>()
        players.addAll(whitelist.players)
        when (operation) {
            Operation.ADD -> {
                if (players.contains(value)) {
                    return Result.PLAYER_ALREADY_EXISTS
                }
                players.add(value)
            }

            Operation.REMOVE -> {
                if (!players.contains(value)) {
                    return Result.PLAYER_NOT_EXIST
                }
                players.remove(value)
            }
        }
        players.sort()
        val json = gson.toJson(
            WhitelistImpl(
                players,
                whitelistName
            )
        )
        val path = Path(Util.joinFilePaths("whitelists", "${whitelistName}.json"))
        try {
            if (Files.exists(path)) {
                val writer = FileWriter(path.toFile())
                writer.write(json)
                writer.flush()
                writer.close()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            init()
            return Result.FAIL
        }
        init()
        return when (operation) {
            Operation.ADD -> Result.WHITELIST_ADDED
            Operation.REMOVE -> Result.WHITELIST_REMOVED
        }
    }


    fun createWhitelist(name: String) {//todo
        TODO()
    }

    fun deleteWhiteList(name: String) {//todo
        TODO()
    }

    enum class Operation {
        ADD, REMOVE
    }

    data class SearchResult(val ratio: Int, val playerName: String)

}