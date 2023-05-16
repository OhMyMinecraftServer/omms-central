package net.zhuruoling.omms.central.whitelist

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import net.zhuruoling.omms.central.util.Util
import java.io.File
import java.nio.charset.Charset

@Serializable
class WhitelistImpl(
    @field:SerializedName("players") private var players: List<String>,
    @field:SerializedName("name") private var name: String
) : Whitelist() {
    override fun getName() = name

    override fun contains(player: String) = players.contains(player)


    override fun getPlayers(): List<String> = players

    @Throws(PlayerAlreadyExistsException::class)
    override fun addPlayer(player: String) {
        if (players.contains(player)) throw PlayerAlreadyExistsException(name, player)
        val buf: MutableList<String> = ArrayList(players)
        buf.add(player)
        players = buf
    }

    @Throws(PlayerNotFoundException::class)
    override fun removePlayer(player: String) {
        if (!players.contains(player)) throw PlayerNotFoundException(name, player)
        val buf: MutableList<String> = ArrayList(players)
        buf.remove(player)
        players = buf
    }

    override fun saveModifiedBuffer() {
        val file = File(Util.joinFilePaths("whitelists", "$name.json"))
        if (file.exists()) {
            file.delete()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writer(Charset.forName("UTF-8")).use {
            Util.gson.toJson(this, this::class.java, it)
        }
    }

    override fun deleteWhitelist() {
        val file = File(Util.joinFilePaths("whitelists", "$name.json"))
        if (file.exists()) {
            file.delete()
        }
    }

    override fun toString(): String {
        return "WhitelistImpl{" +
                "players=" + players +
                ", name='" + name + '\'' +
                '}'
    }
}
