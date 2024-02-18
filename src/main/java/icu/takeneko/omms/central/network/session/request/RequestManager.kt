package icu.takeneko.omms.central.network.session.request

import icu.takeneko.omms.central.network.session.handler.RequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.EndRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.announcement.CreateAnnouncementRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.announcement.DeleteAnnouncementRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.announcement.GetAnnouncementRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.announcement.ListAnnouncementRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.controller.*
import icu.takeneko.omms.central.network.session.handler.builtin.permission.*
import icu.takeneko.omms.central.network.session.handler.builtin.system.GetAllRunnerRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.system.GetRunnerOutputRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.system.GetSysinfoRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.system.RunSystemCommandRequestHandler
import icu.takeneko.omms.central.network.session.handler.builtin.whitelist.*
import icu.takeneko.omms.central.plugin.callback.RequestManagerLoadCallback
import icu.takeneko.omms.central.util.Manager
import icu.takeneko.omms.central.util.StringPair
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


object RequestManager : Manager() {
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
        pluginRequestTable.add(pairOf(pluginId, request))
    }

    fun unRegisterPluginRequest(pluginId: String, request: String) {
        if (requestTable.containsKey(request)) {
            pluginRequestTable.forEach {
                if (it.a() == pluginId && it.b() == request) {
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
            if (it.a() == pluginId) {
                deleted.add(it)
                requestTable.remove(it.b())
                return@forEach
            }
        }
        pluginRequestTable.removeAll(deleted)
    }


    private fun pairOf(a: String, b: String) = StringPair(a, b)


}


val builtinRequestMap = mutableMapOf(
    "PERMISSION_DELETE" to DeletePermissionRequestHandler(),
    "SYSTEM_GET_ALL_RUNNER" to GetAllRunnerRequestHandler(),
    "SYSTEM_RUN_COMMAND" to RunSystemCommandRequestHandler(),
    "CONTROLLER_CREATE" to CreateControllerRequestHandler(),
    "PERMISSION_GRANT" to GrantPermissionRequestHandler(),
    "WHITELIST_LIST" to ListWhitelistRequestHandler(),
    "WHITELIST_ADD" to AddToWhitelistRequestHandler(),
    "PERMISSION_DENY" to DenyPermissionRequestHandler(),
    "WHITELIST_REMOVE" to RemoveFromWhitelistHandler(),
    "CONTROLLER_GET_STATUS" to GetControllerStatusRequestHandler(),
    "CONTROLLER_EXECUTE_COMMAND" to SendCommandToControllerRequestHandler(),
    "ANNOUNCEMENT_LIST" to ListAnnouncementRequestHandler(),
    "ANNOUNCEMENT_DELETE" to DeleteAnnouncementRequestHandler(),
    "WHITELIST_DELETE" to DeleteWhitelistRequestHandler(),
    "PERMISSION_CREATE" to CreatePermissionRequestHandler(),
    "SYSTEM_GET_RUNNER_OUTPUT" to GetRunnerOutputRequestHandler(),
    "CONTROLLER_LIST" to ListControllersRequestHandler(),
    "ANNOUNCEMENT_GET" to GetAnnouncementRequestHandler(),
    "ANNOUNCEMENT_CREATE" to CreateAnnouncementRequestHandler(),
    "END" to EndRequestHandler(),
    "WHITELIST_CREATE" to CreateWhitelistRequestHandler(),
    "CONTROLLER_GET" to GetControllerRequestHandler(),
    "WHITELIST_GET" to GetWhitelistRequestHandler(),
    "SYSTEM_GET_INFO" to GetSysinfoRequestHandler(),
    "PERMISSION_LIST" to ListPermissionRequestHandler(),
    "CONTROLLER_LAUNCH_CONSOLE" to LaunchControllerConsoleRequestHandler(),
    "CONTROLLER_END_CONSOLE" to EndControllerConsoleRequestHandler(),
    "CONTROLLER_INPUT_CONSOLE" to SendControllerConsoleInputRequestHandler()
)
