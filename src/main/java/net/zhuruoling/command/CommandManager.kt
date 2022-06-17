package net.zhuruoling.command

import net.zhuruoling.handler.CommandHandler
import net.zhuruoling.handler.CommandHandlerImpl
import net.zhuruoling.handler.PluginCommandHandler
import net.zhuruoling.plugin.PluginManager
import net.zhuruoling.util.CommandAlreadyExistsException
import net.zhuruoling.util.CommandNotExistException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object CommandManager {
    val logger:Logger = LoggerFactory.getLogger("CommandManager")
    var commandTable:HashMap<String,CommandHandler> = java.util.HashMap()
    fun registerCommand(command: String, handler:CommandHandlerImpl) {
        if (commandTable.containsKey(command)) {
            throw CommandAlreadyExistsException("Command $command already registered by ${commandTable[command]?.register}")
        }
        commandTable[command] = handler
    }

    fun registerCommand(command: String, handler:PluginCommandHandler) {

        if (commandTable.containsKey(command)) {
            throw CommandAlreadyExistsException("Command $command already registered by ${commandTable[command]?.register}")
        }
        commandTable[command] = handler
        PluginManager.registerPluginCommand(handler.pluginName,command)
    }

    fun getCommandHandler(command: String): CommandHandler? {
        if (!commandTable.containsKey(command))
            throw CommandNotExistException("Command $command does not exist.")
        return commandTable[command]
    }

    fun unregisterCommand(command: String) {
        logger.info("Unregistering command $command")
        if (commandTable.containsKey(command)){
            commandTable.remove(command)
            return
        }
        throw CommandNotExistException("Command $command does not exist.")
    }
}
