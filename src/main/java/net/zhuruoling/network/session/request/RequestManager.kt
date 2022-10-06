package net.zhuruoling.network.session.request

import net.zhuruoling.network.session.handler.RequestHandler
import net.zhuruoling.util.StringPair
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Hashtable
import java.util.NoSuchElementException

object RequestManager {
    val logger: Logger = LoggerFactory.getLogger("RequestManager")
    private var requestTable: Hashtable<String, RequestHandler> = Hashtable()
    private val pluginRequestTable: MutableList<StringPair> = mutableListOf()
    fun registerRequest(command: String, handler: RequestHandler) {
        if (requestTable.containsKey(command)) {
            throw RequestAlreadyExistsException("Command $command already registered by ${requestTable[command]?.register}")
        }
        requestTable[command] = handler
    }

    fun getRequestHandler(request: String): RequestHandler? {
        if (!requestTable.containsKey(request))
            throw NoSuchElementException("Command $request does not exist.")
        return requestTable[request]
    }

    fun unregisterCommand(command: String) {
        logger.info("Unregistering command $command")
        if (requestTable.containsKey(command)) {
            requestTable.remove(command)
            return
        }
        throw CanNotFindThatFuckingRequestException("Command $command does not exist.")
    }

    fun registerPluginRequest(request: String, pluginId: String, handler: RequestHandler, override: Boolean = false) {
        if (!override && requestTable.containsKey(request)) {
            throw RequestAlreadyExistsException("Command $request already registered by ${requestTable[request]?.register}")
        }
        requestTable[request] = handler
        pluginRequestTable.add(pairOf(pluginId, request))
    }

    fun unRegisterPluginRequest(pluginId: String, request: String) {
        if (requestTable.containsKey(request)) {
            pluginRequestTable.forEach {
                if (it.a == pluginId && it.b == request) {
                    pluginRequestTable.remove(it)
                    requestTable.remove(request)
                    return
                }
            }
        }
    }

    fun unRegisterPluginRequest(pluginId: String) {
        val deleted = mutableListOf<StringPair>()
        pluginRequestTable.forEach {
            if (it.a == pluginId) {
                deleted.add(it)
                requestTable.remove(it.b)
                return@forEach
            }
        }
        pluginRequestTable.removeAll(deleted)
    }


    fun pairOf(a: String, b: String) = StringPair(a, b)


}
