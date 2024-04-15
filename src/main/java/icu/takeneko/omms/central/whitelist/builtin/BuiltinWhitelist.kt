package icu.takeneko.omms.central.whitelist.builtin

import icu.takeneko.omms.central.util.Util
import icu.takeneko.omms.central.whitelist.PlayerAlreadyExistsException
import icu.takeneko.omms.central.whitelist.PlayerNotFoundException
import icu.takeneko.omms.central.whitelist.ProxyableWhitelist
import icu.takeneko.omms.central.whitelist.Whitelist
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

class BuiltinWhitelist(data: BuiltinWhitelistData) : ProxyableWhitelist {
    private val players: MutableList<String> = data.players.toMutableList()
    private val name: String = data.name
    private val aliases: MutableList<String> = data.aliases.toMutableList()

    override fun getName() = name

    override fun init() {}

    override fun getAliases(): List<String> {
        return aliases
    }

    override fun contains(player: String) = player in players

    override fun getPlayers(): List<String> = players

    override fun addPlayer(player: String) {
        if (players.contains(player)) throw PlayerAlreadyExistsException(
            name,
            player
        )
        players += player
    }

    override fun removePlayer(player: String) {
        if (!players.contains(player)) throw PlayerNotFoundException(
            name,
            player
        )
        players -= player
    }

    override fun saveModifiedBuffer() {
        val file = Util.fileOf("whitelists", "$name.json")
        if (file.exists()) {
            file.delete()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writer(Charset.forName("UTF-8")).use {
            it.write(json.encodeToString(BuiltinWhitelistData(name, aliases, players)))
        }
    }

    override fun deleteWhitelist() {
        val file = Util.fileOf("whitelists", "$name.json")
        if (file.exists()) {
            file.delete()
        }
    }

    override fun onDelegateRemove(instance: Whitelist) {
        if (instance.name in aliases) {
            aliases -= instance.name
            saveModifiedBuffer()
        }
    }

    override fun onDelegateCreate(instance: Whitelist) {
        if (instance.name !in aliases) {
            aliases += instance.name
            saveModifiedBuffer()
        }
    }

    companion object {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = true
        }
    }
}