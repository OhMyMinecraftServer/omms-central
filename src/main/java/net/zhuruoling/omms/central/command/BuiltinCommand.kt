package net.zhuruoling.omms.central.command

import com.google.gson.Gson
import com.mojang.brigadier.context.CommandContext
import net.zhuruoling.omms.central.GlobalVariable.config
import net.zhuruoling.omms.central.GlobalVariable.udpBroadcastSender
import net.zhuruoling.omms.central.network.ChatbridgeImplementation
import net.zhuruoling.omms.central.network.chatbridge.Broadcast
import net.zhuruoling.omms.central.network.http.routes.sendToAllWS
import net.zhuruoling.omms.central.util.Util
import net.zhuruoling.omms.central.util.whitelistPrettyPrinting
import net.zhuruoling.omms.central.whitelist.PlayerAlreadyExistsException
import net.zhuruoling.omms.central.whitelist.PlayerNotFoundException
import net.zhuruoling.omms.central.whitelist.WhitelistManager
import net.zhuruoling.omms.central.whitelist.WhitelistManager.addToWhiteList
import net.zhuruoling.omms.central.whitelist.WhitelistManager.createWhitelist
import net.zhuruoling.omms.central.whitelist.WhitelistManager.deleteWhiteList
import net.zhuruoling.omms.central.whitelist.WhitelistManager.getWhitelistNames
import net.zhuruoling.omms.central.whitelist.WhitelistManager.hasWhitelist
import net.zhuruoling.omms.central.whitelist.WhitelistManager.queryInAllWhitelist
import net.zhuruoling.omms.central.whitelist.WhitelistManager.searchInWhitelist
import net.zhuruoling.omms.central.whitelist.WhitelistNotExistException
import java.util.function.Consumer

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
    literal("remove") {
        wordArgument("whitelist") {
            wordArgument("player") {
                execute {
                    val whitelist = getStringArgument("whitelist")
                    val player = getStringArgument("player")
                    try {
                        WhitelistManager.removeFromWhiteList(whitelist, player)
                        sendFeedback("Successfully removed $player from $whitelist")
                        0
                    } catch (e: WhitelistNotExistException) {
                        sendError("Whitelist ${e.whitelistName} not exist")
                        1
                    } catch (e: PlayerNotFoundException) {
                        sendError("Player ${e.player} not exist.")
                        1
                    }
                }
            }
        }
    }
    literal("search") {
        wordArgument("whitelist") {
            wordArgument("player") {
                execute {
                    val whitelist = getStringArgument("whitelist")
                    val player = getStringArgument("player")
                    if (whitelist == "all") {
                        getWhitelistNames().forEach(Consumer<String> { s: String? ->
                            searchWhitelist(
                                player,
                                s!!, this
                            )
                        })
                        return@execute 0
                    }
                    if (!hasWhitelist(whitelist)) {
                        sendError("Specified whitelist does not exist.")
                    }
                    searchWhitelist(player, whitelist, this)
                    1
                }
            }
        }
    }
    literal("list") {
        execute {
            WhitelistManager.forEach { (_, value) ->
                whitelistPrettyPrinting(value)
                    .split("\n".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toList()
                    .forEach(this::sendFeedback)
            }
            1
        }
    }
    literal("create") {
        wordArgument("name") {
            execute {
                val name = getStringArgument("name")
                if (hasWhitelist(name)) {
                    sendError("Whitelist %s already exists".formatted(name))
                    return@execute 1
                }
                try {
                    createWhitelist(name)
                } catch (e: Exception) {
                    sendError(e.stackTraceToString())
                }
                sendFeedback("Done.")
                1
            }
        }
    }
    literal("delete") {
        wordArgument("name") {
            execute {
                val name = getStringArgument("name")
                if (!hasWhitelist(name)) {
                    sendError("Whitelist $name not exist")
                    return@execute 1
                }
                try {
                    deleteWhiteList(name)
                } catch (e: WhitelistNotExistException) {
                    sendError(e.stackTraceToString())
                }
                sendFeedback("Done.")
                1
            }
        }
    }
}

val broadcastCommand = LiteralCommand("broadcast") {
    greedyStringArgument("text") {
        execute {
            if (config?.chatbridgeImplementation == null) {
                sendFeedback("Chatbridge disabled.")
                return@execute 0
            }
            val text = getStringArgument("text")
            sendFeedback("Sending message:$text")
            val broadcast = Broadcast()
            broadcast.setChannel("GLOBAL")
            broadcast.setContent(text)
            broadcast.setPlayer(Util.randomStringGen(8))
            broadcast.setServer("OMMS CENTRAL")
            when (config!!.chatbridgeImplementation) {
                ChatbridgeImplementation.UDP -> udpBroadcastSender!!.addToQueue(
                    Util.TARGET_CHAT,
                    Gson().toJson(broadcast, Broadcast::class.java)
                )

                ChatbridgeImplementation.WS -> sendToAllWS(broadcast)
            }
            1
        }
    }
}

private fun searchWhitelist(player: String, s: String, context: CommandContext<CommandSourceStack>) {
    val result = try {
        searchInWhitelist(s, player)
    } catch (e: WhitelistNotExistException) {
        context.sendFeedback("Whitelist $s not found.")
        return
    }
    if (result.isEmpty()) {
        context.sendFeedback("No valid results in whitelist $s.")
    } else {
        context.sendFeedback("Search result in whitelist $s:")
        result.forEach {
            context.sendFeedback("\t${it.playerName}")
        }
    }
}