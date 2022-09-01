package net.zhuruoling.request

import net.zhuruoling.handler.RequestHandler
import net.zhuruoling.plugin.RequestServerInterface
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.BiConsumer

object RequestManager {
    val logger:Logger = LoggerFactory.getLogger("CommandManager")
    var commandTable:HashMap<String, RequestHandler> = java.util.HashMap()
    fun registerRequest(command: String, handler: RequestHandler) {
        if (commandTable.containsKey(command)) {
            throw RequestAlreadyExistsException("Command $command already registered by ${commandTable[command]?.register}")
        }
        commandTable[command] = handler
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
