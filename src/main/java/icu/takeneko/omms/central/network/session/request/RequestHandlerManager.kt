package icu.takeneko.omms.central.network.session.request

import icu.takeneko.omms.central.network.session.handler.RequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.EndRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.chatbridge.SetChatMessagePassthroughRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.chatbridge.GetChatHistoryRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.chatbridge.SendBroadcastRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.controller.*
import icu.takeneko.omms.central.network.session.handler.builtin.permission.*
import icu.takeneko.omms.central.network.session.handler.builtin.system.GetSysinfoRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.whitelist.*
import icu.takeneko.omms.central.plugin.callback.RequestManagerLoadCallback
import icu.takeneko.omms.central.foundation.Manager
import icu.takeneko.omms.central.network.session.RequestAlreadyExistsException
import icu.takeneko.omms.central.network.session.handler.builtin.chatbridge.GetChatbridgeImplementationRequestHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


private typealias StringPair = Pair<String, String>

object RequestHandlerManager : Manager() {
    val logger: Logger = LoggerFactory.getLogger("RequestManager")
    private var requestTable: Hashtable<String, RequestHandler> = Hashtable()
    private val pluginRequestTable: MutableList<StringPair> = mutableListOf()

    fun getAllRegisteredRequest() = requestTable

    override fun init() {
        builtinRequestMap.forEach {
            registerRequest(it.key, it.value)
        }
        RequestManagerLoadCallback.INSTANCE.invokeAll(this)
    }

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
        pluginRequestTable.add(pluginId to request)
    }

    fun unRegisterPluginRequest(pluginId: String, request: String) {
        if (requestTable.containsKey(request)) {
            pluginRequestTable.forEach {
                if (it.first == pluginId && it.second == request) {
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
            if (it.first == pluginId) {
                deleted.add(it)
                requestTable.remove(it.second)
                return@forEach
            }
        }
        pluginRequestTable.removeAll(deleted)
    }
}


val builtinRequestMap = mutableMapOf(
    "PERMISSION_DELETE" to DeletePermissionRequestHandler(),
    "CONTROLLER_CREATE" to CreateControllerRequestHandler(),
    "PERMISSION_GRANT" to GrantPermissionRequestHandler(),
    "WHITELIST_LIST" to ListWhitelistRequestHandler(),
    "WHITELIST_ADD" to AddToWhitelistRequestHandler(),
    "PERMISSION_DENY" to DenyPermissionRequestHandler(),
    "WHITELIST_REMOVE" to RemoveFromWhitelistHandler(),
    "CONTROLLER_GET_STATUS" to GetControllerStatusRequestHandler(),
    "CONTROLLER_EXECUTE_COMMAND" to SendCommandToControllerRequestHandler(),
    "WHITELIST_DELETE" to DeleteWhitelistRequestHandler(),
    "PERMISSION_CREATE" to CreatePermissionRequestHandler(),
    "CONTROLLER_LIST" to ListControllersRequestHandler(),
    "END" to EndRequestHandler(),
    "WHITELIST_CREATE" to CreateWhitelistRequestHandler(),
    "WHITELIST_GET" to GetWhitelistRequestHandler(),
    "SYSTEM_GET_INFO" to GetSysinfoRequestHandler(),
    "PERMISSION_LIST" to ListPermissionRequestHandler(),
    "CONTROLLER_LAUNCH_CONSOLE" to LaunchControllerConsoleRequestHandler(),
    "CONTROLLER_END_CONSOLE" to EndControllerConsoleRequestHandler(),
    "CONTROLLER_INPUT_CONSOLE" to SendControllerConsoleInputRequestHandler(),
    "CONTROLLER_CONSOLE_COMPLETE" to RequestCommandCompletionRequestHandler(),
    "SEND_BROADCAST" to SendBroadcastRequestHandler,
    "GET_CHAT_HISTORY" to GetChatHistoryRequestHandler,
    "SET_CHAT_PASSTHROUGH_STATE" to SetChatMessagePassthroughRequestHandler,
    "GET_CHATBRIDGE_IMPL" to GetChatbridgeImplementationRequestHandler
)
