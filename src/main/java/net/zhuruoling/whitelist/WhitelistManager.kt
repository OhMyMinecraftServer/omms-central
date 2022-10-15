package net.zhuruoling.whitelist

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import me.xdrop.fuzzywuzzy.FuzzySearch
import net.zhuruoling.util.Result
import net.zhuruoling.util.Util
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import kotlin.io.path.Path

object WhitelistManager {

    private val whitelistTable = HashMap<String, Whitelist>()
    private val gson = GsonBuilder().serializeNulls().create()
    private val logger = LoggerFactory.getLogger("WhitelistManager")
    fun init() {
        whitelistTable.clear()
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
                val whitelist = gson.fromJson(FileReader(it.toFile()), Whitelist::class.java)
                if (it.toFile().name != "${whitelist.name}.json") {
                    logger.warn("Whitelist name(${whitelist.name}) does not match with file name(${it.toFile().name}),fixing.")
                    Files.delete(it)
                    Files.write(
                        Path(Util.joinFilePaths("whitelists", "${whitelist.name}.json")),
                        gson.toJson(whitelist, Whitelist::class.java).encodeToByteArray()
                    )
                }
                if (whitelistTable.containsKey(whitelist.getName())) {
                    throw RuntimeException("Duplicated whitelist name(${whitelist.name}).")
                }
                whitelistTable[whitelist.getName()] = whitelist
            } catch (e: JsonParseException) {
                throw java.lang.RuntimeException("Illegal file format.", e)
            } catch (e: Exception) {
                throw java.lang.RuntimeException("Cannot load whitelist file(${it.toFile().absolutePath}).", e)
            }
        }
    }

    fun queryWhitelist(whitelistName: String?, value: String): Result {
        val whitelist = whitelistTable[whitelistName] ?: return Result.WHITELIST_NOT_EXIST
        if (whitelist.getPlayers().contains(value)) {
            return Result.OK
        }
        return Result.NO_SUCH_PLAYER
    }

    fun queryInAllWhitelist(player: String): MutableList<String> {
        val list = mutableListOf<String>()
        whitelistTable.forEach {
            if (it.value.getPlayers().contains(player)) {
                list.add(it.key)
            }
        }
        return list
    }

    fun getWhitelist(whitelistName: String): Whitelist? {
        return whitelistTable[whitelistName]
    }

    fun getWhitelists(): MutableCollection<Whitelist> {
        return whitelistTable.values
    }

    fun isNoWhitelist(): Boolean {
        return whitelistTable.isEmpty()
    }

    fun hasWhitelist(whitelistName: String): Boolean {
        return whitelistTable.containsKey(whitelistName)
    }

    fun forEach(action: (Map.Entry<String, Whitelist>) -> Unit) {
        whitelistTable.forEach {
            action.invoke(it)
        }
    }

    @Synchronized
    fun searchInWhitelist(whitelistName: String, playerName: String): List<SearchResult>? {
        val whitelist = whitelistTable[whitelistName] ?: return null
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
        return whitelistTable.keys
    }


    @Synchronized
    fun addToWhiteList(whitelistName: String, value: String): Result {
        return performWhitelistModify(whitelistName, value, Operation.ADD)
    }

    @Synchronized
    fun removeFromWhiteList(whitelistName: String, value: String): Result {
        return performWhitelistModify(whitelistName, value, Operation.REMOVE)
    }

    private fun performWhitelistModify(whitelistName: String, value: String, operation: Operation): Result {
        val whitelist = whitelistTable[whitelistName] ?: return Result.WHITELIST_NOT_EXIST
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
                    return Result.NO_SUCH_PLAYER
                }
                players.remove(value)
            }
        }
        val json = gson.toJson(Whitelist(players.toTypedArray(), whitelistName))
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
        return Result.OK


    }

    fun createWhitelist(name: String): Result {
        return Result.FAIL
    }

    fun deleteWhiteList(name: String): Result {
        return Result.FAIL
    }

    enum class Operation {
        ADD, REMOVE
    }

    data class SearchResult(val ratio: Int, val playerName: String)

}