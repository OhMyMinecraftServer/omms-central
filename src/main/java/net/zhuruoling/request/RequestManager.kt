package net.zhuruoling.request

import net.zhuruoling.handler.RequestHandler
import net.zhuruoling.handler.RequestHandlerImpl
import net.zhuruoling.handler.PluginRequestHandler
import net.zhuruoling.plugin.PluginManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object RequestManager {
    val logger:Logger = LoggerFactory.getLogger("CommandManager")
    var commandTable:HashMap<String, RequestHandler> = java.util.HashMap()
    fun registerRequest(command: String, handler: RequestHandlerImpl) {
        if (commandTable.containsKey(command)) {
            throw RequestAlreadyExistsException("Command $command already registered by ${commandTable[command]?.register}")
        }
        commandTable[command] = handler
    }

    fun registerRequest(request: String, handler: PluginRequestHandler) {

        if (commandTable.containsKey(request)) {
            throw RequestAlreadyExistsException("Command $request already registered by ${commandTable[request]?.register}")
        }
        commandTable[request] = handler
        PluginManager.registerPluginCommand(handler.pluginName,request)
    }

    fun getRequestHandler(request: String): RequestHandler? {
        if (!commandTable.containsKey(request))
            throw CanNotFindThatFuckingRequestException("Command $request does not exist.")
        return commandTable[request]
    }

    fun unregisterCommand(command: String) {
        logger.info("Unregistering command $command")
        if (commandTable.containsKey(command)){
            commandTable.remove(command)
            return
        }
        throw CanNotFindThatFuckingRequestException("Command $command does not exist.")
    }
}
