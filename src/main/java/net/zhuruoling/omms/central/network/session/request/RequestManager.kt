package net.zhuruoling.omms.central.network.session.request

import net.zhuruoling.omms.central.network.session.handler.RequestHandler
import net.zhuruoling.omms.central.util.StringPair
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Hashtable
import java.util.NoSuchElementException

object RequestManager {
    val logger: Logger = LoggerFactory.getLogger("RequestManager")
    private var requestTable: Hashtable<String, RequestHandler> = Hashtable()
    private val pluginRequestTable: MutableList<StringPair> = mutableListOf()

    fun getAllRegisteredRequest() = requestTable

    fun registerRequest(requestName: String, handler: RequestHandler) {
        logger.debug("Registering $requestName with ${handler.javaClass.name}")
        if (requestTable.containsKey(requestName)) {
            throw RequestAlreadyExistsException("Command $requestName already registered by ${requestTable[requestName]?.register}")
        }
        requestTable[requestName] = handler
    }

    fun getRequestHandler(request: String): RequestHandler? {
        if (!requestTable.containsKey(request))
            throw NoSuchElementException("Command $request does not exist.")
        return requestTable[request]
    }

    fun unregisterRequest(requestName: String) {
        logger.debug("Unregistering command $requestName")
        if (requestTable.containsKey(requestName)) {
            requestTable.remove(requestName)
            return
        }
        throw NoSuchElementException("Command $requestName does not exist.")
    }

    fun registerPluginRequest(request: String, pluginId: String, handler: RequestHandler, override: Boolean = false) {
        logger.debug("[$pluginId] Registering $request with ${handler.javaClass.name}")
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


    private fun pairOf(a: String, b: String) = StringPair(a, b)


}
