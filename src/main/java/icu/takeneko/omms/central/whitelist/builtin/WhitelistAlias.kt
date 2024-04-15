package icu.takeneko.omms.central.whitelist.builtin

import icu.takeneko.omms.central.whitelist.ProxyableWhitelist
import icu.takeneko.omms.central.whitelist.Whitelist

class WhitelistAlias(private val parent: ProxyableWhitelist, private val name:String): Whitelist {
    override fun getName() = name
    override fun init() {
        //parent.onDelegateCreate(this)
    }

    override fun contains(player: String) = parent.contains(player)

    override fun getPlayers(): List<String> = parent.players

    override fun addPlayer(player: String) = parent.addPlayer(player)

    override fun removePlayer(player: String) = parent.removePlayer(player)

    override fun saveModifiedBuffer() = parent.saveModifiedBuffer()

    override fun deleteWhitelist() = parent.onDelegateRemove(this)
}