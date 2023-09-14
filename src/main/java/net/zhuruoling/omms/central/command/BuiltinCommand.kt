package net.zhuruoling.omms.central.command

import com.google.gson.Gson
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.zhuruoling.omms.central.GlobalVariable.args
import net.zhuruoling.omms.central.GlobalVariable.config
import net.zhuruoling.omms.central.GlobalVariable.udpBroadcastSender
import net.zhuruoling.omms.central.announcement.AnnouncementManager
import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.main.CentralServer
import net.zhuruoling.omms.central.network.ChatbridgeImplementation
import net.zhuruoling.omms.central.network.chatbridge.Broadcast
import net.zhuruoling.omms.central.network.http.routes.sendToAllWS
import net.zhuruoling.omms.central.permission.PermissionManager
import net.zhuruoling.omms.central.plugin.PluginManager
import net.zhuruoling.omms.central.plugin.metadata.PluginDependencyRequirement
import net.zhuruoling.omms.central.util.Util
import net.zhuruoling.omms.central.util.printRuntimeEnv
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
import java.lang.management.ManagementFactory

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
                        getWhitelistNames().forEach {
                            searchWhitelist(
                                player,
                                it, this
                            )
                        }
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
                    sendError("Whitelist %s already exists", name)
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
val stopCommand = LiteralCommand("stop") {
    execute {
        CentralServer.stop()
        1
    }
}

val reloadCommand = LiteralCommand("reload") {
    execute {
        CommandManager.INSTANCE.clear()
        PermissionManager.init()
        ControllerManager.init()
        AnnouncementManager.init()
        WhitelistManager.init()
        CommandManager.INSTANCE.reload()
        1
    }
}

val statusCommand = LiteralCommand("status") {
    execute {
        printRuntimeEnv()
        val runtime = ManagementFactory.getRuntimeMXBean()
        sendFeedback("Java VM Info: %s %s %s", runtime.vmVendor, runtime.vmName, runtime.vmVersion)
        sendFeedback(
            "Java VM Spec Info: %s %s %s",
            runtime.specVendor,
            runtime.specName,
            runtime.specVersion
        )
        sendFeedback("Java version: %s", System.getProperty("java.version"))
        val upTime = runtime.uptime / 1000.0
        sendFeedback(String.format("Uptime: %.3fS", upTime))
        val memoryMXBean = ManagementFactory.getMemoryMXBean()
        val heapMemoryUsage = memoryMXBean.heapMemoryUsage
        val nonHeapMemoryUsage = memoryMXBean.nonHeapMemoryUsage
        val maxMemory = (heapMemoryUsage.max + nonHeapMemoryUsage.max) / 1024.0 / 1024.0
        val usedMemory = (heapMemoryUsage.used + nonHeapMemoryUsage.used) / 1024.0 / 1024.0
        sendFeedback(String.format("Memory usage: %.3fMiB/%.3fMiB", usedMemory, maxMemory))
        Util.listAllByCommandSource(this.source)
        val threadGroup = Thread.currentThread().threadGroup
        val count = threadGroup.activeCount()
        val threads = arrayOfNulls<Thread>(count)
        threadGroup.enumerate(threads)
        sendFeedback("Thread Count: %d", count)
        sendFeedback("Threads:")
        threads.forEach {
            sendFeedback("\t+ %s %d %s%s", it!!.name, it.id, if (it.isDaemon) "DAEMON " else "", it.state.name)
        }
        sendFeedback("Java VM Arguments:")
        runtime.inputArguments.forEach {
            sendFeedback("\t%s", it)
        }
        sendFeedback("main() Arguments:")
        args.forEach {
            sendFeedback("\t%s", it)
        }
        1
    }
}

val helpCommand = LiteralCommand("help") {
    execute {
        val dispatcher = CommandManager.INSTANCE.commandDispatcher
        val usages =
            dispatcher.getAllUsage(dispatcher.root, CommandSourceStack(CommandSourceStack.Source.INTERNAL), false)
        for (usage in usages) {
            sendFeedback(usage)
        }
        1
    }
}

val permissionCommand = LiteralCommand("permission") {

}

val controllerCommand = LiteralCommand("controller") {

}

val announcementCommand = LiteralCommand("announcement") {
    literal("list") {
        execute {

            1
        }
    }
    literal("create") {
        execute {

            1
        }
    }
}

val pluginCommand = LiteralCommand("plugin") {
    literal("list") {
        execute {
            for (instance in PluginManager) {
                val metadata = instance.pluginMetadata
                sendFeedback(":: %s%s ::", metadata.id, if (metadata.version != null) " ${metadata.version}" else "")
                if (metadata.author != null) {
                    sendFeedback("    - Author: %s", metadata.author)
                }
                if (metadata.link != null) {
                    sendFeedback("    - Link: %s", metadata.link)
                }
                if (metadata.pluginDependencies != null && metadata.pluginDependencies.isNotEmpty()) {
                    sendFeedback("    - Requires: %s", joinToDependencyString(metadata.pluginDependencies))
                }
                if (metadata.pluginMainClass != null) {
                    sendFeedback("    - Plugin Initializer: %s", metadata.pluginMainClass)
                }
                if (metadata.pluginRequestHandlers != null && metadata.pluginRequestHandlers.isNotEmpty()) {
                    sendFeedback("    - Plugin Request Handlers: %s", metadata.pluginRequestHandlers.joinToString(" "))
                }
            }
            1
        }
    }
}


fun registerBuiltinCommand(dispatcher: CommandDispatcher<CommandSourceStack>) {
    dispatcher.register(whitelistCommand)
    dispatcher.register(broadcastCommand)
    dispatcher.register(stopCommand)
    dispatcher.register(reloadCommand)
    dispatcher.register(statusCommand)
    dispatcher.register(helpCommand)
    dispatcher.register(permissionCommand)
    dispatcher.register(controllerCommand)
    dispatcher.register(announcementCommand)
    dispatcher.register(pluginCommand)
}

private fun CommandDispatcher<S>.register(command: LiteralCommand) {
    register(command.node)
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

private fun joinToDependencyString(pluginDependencyRequirements: List<PluginDependencyRequirement>): String {
    return pluginDependencyRequirements.joinToString(separator = " ") { it.toString() }
}

