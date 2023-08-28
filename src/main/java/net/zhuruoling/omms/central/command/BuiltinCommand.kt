package net.zhuruoling.omms.central.command

import net.zhuruoling.omms.central.whitelist.PlayerAlreadyExistsException
import net.zhuruoling.omms.central.whitelist.WhitelistManager
import net.zhuruoling.omms.central.whitelist.WhitelistManager.addToWhiteList
import net.zhuruoling.omms.central.whitelist.WhitelistManager.getWhitelistNames
import net.zhuruoling.omms.central.whitelist.WhitelistManager.queryInAllWhitelist
import net.zhuruoling.omms.central.whitelist.WhitelistNotExistException

val whitelistCommand = LiteralCommand("whitelist") {
    literal("get") {
        wordArgument("whitelist") {
            execute {
                val name = getStringArgument("whitelist")
                val w = WhitelistManager.getWhitelist(name) ?: run {
                    sendError("Whitelist $name does not exist.")
                    return@execute 0
                }
                sendFeedback("Whitelist $name")
                w.players.forEach {
                    sendFeedback("\t- $it")
                }
                1
            }
        }
    }
    literal("list") {
        execute {
            val names = getWhitelistNames()
            sendFeedback("${names.size} whitelists(${names.joinToString(", ")}) added to this server.")
            1
        }
    }
    literal("query") {
        wordArgument("player") {
            execute {
                val player = getStringArgument("player")
                val names = queryInAllWhitelist(player)
                if (names.isEmpty()) {
                    sendFeedback("Player $player does not exist in any whitelists.")
                    1
                } else {
                    sendFeedback("Player $player exists in whitelist: $names.")
                    1
                }
            }
        }
    }
    literal("add") {
        wordArgument("whitelist") {
            wordArgument("player") {
                execute {
                    val whitelist = getStringArgument("whitelist")
                    val player = getStringArgument("player")
                    try {
                        addToWhiteList(whitelist, player)
                        sendFeedback("Successfully added $player to $whitelist")
                        0
                    } catch (e: WhitelistNotExistException) {
                        sendError("Whitelist ${e.whitelistName} not exist")
                        1
                    } catch (e: PlayerAlreadyExistsException) {
                        sendError("Player ${e.player} already added to ${e.whitelist} exist")
                        1
                    }
                }
            }
        }
    }
}