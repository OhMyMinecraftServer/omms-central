package net.zhuruoling.omms.central.whitelist

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

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

    }
    override fun toString(): String {
        return "WhitelistImpl{" +
                "players=" + players +
                ", name='" + name + '\'' +
                '}'
    }
}
