package icu.takeneko.omms.central.command.builtin

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import icu.takeneko.omms.central.RunConfiguration
import icu.takeneko.omms.central.SharedObjects
import icu.takeneko.omms.central.command.*
import icu.takeneko.omms.central.command.arguments.ControllerArgumentType
import icu.takeneko.omms.central.command.arguments.PermissionCodeArgumentType
import icu.takeneko.omms.central.command.arguments.PermissionNameArgumentType
import icu.takeneko.omms.central.config.Config
import icu.takeneko.omms.central.console.printControllerStatus
import icu.takeneko.omms.central.controller.Controller
import icu.takeneko.omms.central.controller.ControllerManager
import icu.takeneko.omms.central.controller.console.input.StdinInputSource
import icu.takeneko.omms.central.controller.console.output.StdOutPrintTarget
import icu.takeneko.omms.central.main.CentralServer
import icu.takeneko.omms.central.network.ChatbridgeImplementation
import icu.takeneko.omms.central.network.chatbridge.Broadcast
import icu.takeneko.omms.central.network.chatbridge.send
import icu.takeneko.omms.central.permission.Operation
import icu.takeneko.omms.central.permission.Permission
import icu.takeneko.omms.central.permission.PermissionChange
import icu.takeneko.omms.central.permission.PermissionManager
import icu.takeneko.omms.central.plugin.PluginManager
import icu.takeneko.omms.central.plugin.metadata.PluginDependencyRequirement
import icu.takeneko.omms.central.util.Util
import icu.takeneko.omms.central.util.controllerPrettyPrinting
import icu.takeneko.omms.central.util.printRuntimeEnv
import icu.takeneko.omms.central.util.whitelistPrettyPrinting
import icu.takeneko.omms.central.whitelist.*
import icu.takeneko.omms.central.whitelist.WhitelistManager.addToWhiteList
import icu.takeneko.omms.central.whitelist.WhitelistManager.createWhitelist
import icu.takeneko.omms.central.whitelist.WhitelistManager.deleteWhiteList
import icu.takeneko.omms.central.whitelist.WhitelistManager.getWhitelistNames
import icu.takeneko.omms.central.whitelist.WhitelistManager.hasWhitelist
import icu.takeneko.omms.central.whitelist.WhitelistManager.queryInAllWhitelist
import icu.takeneko.omms.central.whitelist.WhitelistManager.searchInWhitelist
import org.jline.reader.impl.history.DefaultHistory
import org.slf4j.LoggerFactory
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J
import java.lang.management.ManagementFactory
import java.util.concurrent.locks.LockSupport

private val logger = LoggerFactory.getLogger("BuiltinCommand")
private val config = Config.config

val whitelistCommand = LiteralCommand("whitelist") {
    literal("get") {
        whitelistArgument("whitelist") {
            execute {
                val whitelist: Whitelist by this
                sendFeedback("Whitelist ${whitelist.name}")
                whitelist.players.forEach {
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
                val player: String by this
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
        whitelistArgument("whitelist") {
            wordArgument("player") {
                execute {
                    val whitelist: Whitelist by this
                    val player: String by this
                    try {
                        addToWhiteList(whitelist.name, player)
                        sendFeedback("Successfully added $player to ${whitelist.name}")
                        0
                    } catch (e: PlayerAlreadyExistsException) {
                        sendError("Player ${e.player} already added to ${whitelist.name}")
                        1
                    }
                }
            }
        }
    }
    literal("remove") {
        whitelistArgument("whitelist") {
            wordArgument("player") {
                execute {
                    val whitelist: Whitelist by this
                    val player: String by this
                    try {
                        WhitelistManager.removeFromWhiteList(whitelist.name, player)
                        sendFeedback("Successfully removed $player from $whitelist")
                        0
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
                    val whitelist: String by this
                    val player: String by this
                    if (whitelist == "all") {
                        getWhitelistNames().forEach {
                            searchWhitelist(
                                player,
                                it,
                                this
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
                val name: String by this
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
                val name: String by this
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
            if (config.chatbridgeImplementation == ChatbridgeImplementation.DISABLE) {
                sendFeedback("Chatbridge disabled.")
                return@execute 0
            }
            val text = getStringArgument("text")
            sendFeedback("Sending message:$text")
            Broadcast("GLOBAL", text).send()
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
        val src: CommandSourceStack by this
        Util.listAllByCommandSource(src)
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
        RunConfiguration.args.forEach {
            sendFeedback("\t%s", it)
        }
        1
    }
}

val helpCommand = LiteralCommand("help") {
    execute {
        val dispatcher = CommandManager.INSTANCE.commandDispatcher
        val usages =
            dispatcher.getAllUsage(
                dispatcher.root,
                CommandSourceStack(CommandSourceStack.Source.INTERNAL), false
            )
        for (usage in usages) {
            sendFeedback(usage)
        }
        1
    }
}

val permissionCommand = LiteralCommand("permission") {
    literal("list") {
        literal("changes") {
            execute {
                PermissionManager.changes.forEachIndexed { index, it ->
                    sendFeedback("[$index] $it")
                }
                1
            }
        }
        literal("permissions") {
            execute {
                PermissionManager.permissionTable.forEach { t, u ->
                    sendFeedback("Permission Code: $t has those following permissions:")
                    u.forEach {
                        sendFeedback("    - ${it.name}")
                    }
                }
                1
            }
        }
    }
    literal("create") {
        argument("name", StringArgumentType.string()) {
            execute {
                val c = getStringArgument("name")
                val change = PermissionChange(Operation.CREATE, c, mutableListOf())
                PermissionManager.submitPermissionChanges(change)
                sendFeedback("Submitted permission change: $change")
                1
            }
        }

    }
    literal("delete") {
        argument("name", PermissionCodeArgumentType.code()) {
            execute {
                val c = getStringArgument("name")
                val change = PermissionChange(Operation.DELETE, c, mutableListOf())
                PermissionManager.submitPermissionChanges(change)
                sendFeedback("Submitted permission change: $change")
                1
            }
        }
    }
    literal("modify") {
        argument("name", PermissionCodeArgumentType.code()) {
            literal("allow") {
                StringArgumentType.word()
                argument("permissions", PermissionNameArgumentType.permission()) {
                    execute {
                        val name: String by this
                        val permissions: List<Permission> by this
                        val change = PermissionChange(
                            Operation.GRANT,
                            name,
                            permissions
                        )
                        PermissionManager.submitPermissionChanges(change)
                        sendFeedback("Submitted permission change: $change")
                        1
                    }
                }
            }
            literal("deny") {
                argument("permissions", PermissionNameArgumentType.permission()) {
                    execute {
                        val name: String by this
                        val permissions: List<Permission> by this
                        val change = PermissionChange(
                            Operation.DENY,
                            name,
                            permissions
                        )
                        PermissionManager.submitPermissionChanges(change)
                        sendFeedback("Submitted permission change: $change")
                        1
                    }
                }
            }
        }
    }
    literal("apply") {
        execute {
            PermissionManager.applyChanges(this)
            1
        }
    }
}


val controllerCommand = LiteralCommand("controller") {
    literal("execute") {
        argument("controller", ControllerArgumentType.queryable()) {
            greedyStringArgument("command") {
                execute {
                    val controller: Controller by this
                    val command = getStringArgument("command")
                    val result = ControllerManager.sendCommand(controller, command)
                    result.result.forEach {
                        sendFeedback("[${result.controllerId}] $it")
                    }
                    1
                }
            }
        }
    }
    literal("list") {
        execute {
            sendFeedback("")
            ControllerManager.controllers.forEach {
                sendFeedback(controllerPrettyPrinting(it.value))
            }
            1
        }
    }
    literal("console") {
        argument("controller", ControllerArgumentType.queryable()) {
            execute {
                val controller: Controller by this
                val outputTarget = StdOutPrintTarget()
                sendFeedback("Attaching console to controller, use \":q\" to exit console.")
                SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J()
                val console = controller.startControllerConsole(
                    {
                        StdinInputSource(it, getOrCreateControllerHistory(controller.name))
                    },
                    outputTarget,
                    "console"
                )
                console.start()
                while (console.isAlive) {
                    LockSupport.parkNanos(10)
                }
                sendFeedback("Exiting console.")
                SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()
                1
            }
        }

    }
    literal("status") {
        argument("controller", ControllerArgumentType.queryable()) {
            execute {
                val controller: Controller by this
                val src: CommandSourceStack by this
                try {
                    val status = controller.queryControllerStatus()
                    printControllerStatus(src, controller.name, status)
                } catch (e: Exception) {
                    sendError("Error occurred while querying controller status: $e")
                    logger.debug("Error occurred while querying controller status: ${e.stackTraceToString()}")
                }
                1
            }
        }
        literal("all") {
            execute {
                ControllerManager.controllers.values.filter(Controller::isStatusQueryable).forEach {
                    val src: CommandSourceStack by this
                    try {
                        val status = it.queryControllerStatus()
                        printControllerStatus(src, it.name, status)
                    } catch (e: Exception) {
                        sendError("Error occurred while querying controller status: $e")
                        logger.debug("Error occurred while querying controller status: ${e.stackTraceToString()}")
                    }
                }
                1
            }
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
    literal("reload") {
        requires({ it.source != CommandSourceStack.Source.REMOTE }) {
            execute {
                logger.warn("Plugin reloading is highly experimental, in some cases it can cause severe problems.")
                logger.info("Reloading all plugins!")
                PluginManager.reloadAll()
                0
            }
        }
    }
    literal("refresh") {
        execute {
            logger.warn("Plugin refreshing is highly experimental, in some cases it can cause severe problems.")
            logger.info("Refreshing plugins!")
            PluginManager.refreshPlugins()
            0
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

private fun joinToDependencyString(requirements: List<PluginDependencyRequirement>): String {
    return requirements.joinToString(separator = " ") { it.toString() }
}

private fun getOrCreateControllerHistory(controllerId: String): DefaultHistory {
    return SharedObjects.controllerConsoleHistory[controllerId]
        ?: DefaultHistory().run { SharedObjects.controllerConsoleHistory[controllerId] = this;this }
}