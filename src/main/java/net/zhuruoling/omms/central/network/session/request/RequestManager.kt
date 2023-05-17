package net.zhuruoling.omms.central.network.session.request

import net.zhuruoling.omms.central.network.session.handler.RequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.controller.GetControllerRequestHandler
import net.zhuruoling.omms.central.plugin.callback.RequestManagerLoadCallback
import net.zhuruoling.omms.central.util.Manager
import net.zhuruoling.omms.central.util.StringPair
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Hashtable
import java.util.NoSuchElementException


object RequestManager : Manager(){
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


val builtinRequestMap = mutableMapOf(
    "PERMISSION_DELETE" to net.zhuruoling.omms.central.network.session.handler.builtin.permission.DeletePermissionRequestHandler(),
    "SYSTEM_GET_ALL_RUNNER" to net.zhuruoling.omms.central.network.session.handler.builtin.system.GetAllRunnerRequestHandler(),
    "SYSTEM_RUN_COMMAND" to net.zhuruoling.omms.central.network.session.handler.builtin.system.RunSystemCommandRequestHandler(),
    "CONTROLLER_CREATE" to net.zhuruoling.omms.central.network.session.handler.builtin.controller.CreateControllerRequestHandler(),
    "PERMISSION_GRANT" to net.zhuruoling.omms.central.network.session.handler.builtin.permission.GrantPermissionRequestHandler(),
    "WHITELIST_LIST" to net.zhuruoling.omms.central.network.session.handler.builtin.whitelist.ListWhitelistRequestHandler(),
    "WHITELIST_ADD" to net.zhuruoling.omms.central.network.session.handler.builtin.whitelist.AddToWhitelistRequestHandler(),
    "PERMISSION_DENY" to net.zhuruoling.omms.central.network.session.handler.builtin.permission.DenyPermissionRequestHandler(),
    "WHITELIST_REMOVE" to net.zhuruoling.omms.central.network.session.handler.builtin.whitelist.RemoveFromWhitelistHandler(),
    "CONTROLLER_GET_STATUS" to net.zhuruoling.omms.central.network.session.handler.builtin.controller.GetControllerStatusRequestHandler(),
    "CONTROLLER_EXECUTE_COMMAND" to net.zhuruoling.omms.central.network.session.handler.builtin.controller.SendCommandToControllerRequestHandler(),
    "ANNOUNCEMENT_LIST" to net.zhuruoling.omms.central.network.session.handler.builtin.announcement.ListAnnouncementRequestHandler(),
    "ANNOUNCEMENT_DELETE" to net.zhuruoling.omms.central.network.session.handler.builtin.announcement.DeleteAnnouncementRequestHandler(),
    "WHITELIST_DELETE" to net.zhuruoling.omms.central.network.session.handler.builtin.whitelist.DeleteWhitelistRequestHandler(),
    "PERMISSION_CREATE" to net.zhuruoling.omms.central.network.session.handler.builtin.permission.CreatePermissionRequestHandler(),
    "SYSTEM_GET_RUNNER_OUTPUT" to net.zhuruoling.omms.central.network.session.handler.builtin.system.GetRunnerOutputRequestHandler(),
    "CONTROLLER_LIST" to net.zhuruoling.omms.central.network.session.handler.builtin.controller.ListControllersRequestHandler(),
    "ANNOUNCEMENT_GET" to net.zhuruoling.omms.central.network.session.handler.builtin.announcement.GetAnnouncementRequestHandler(),
    "ANNOUNCEMENT_CREATE" to net.zhuruoling.omms.central.network.session.handler.builtin.announcement.CreateAnnouncementRequestHandler(),
    "END" to net.zhuruoling.omms.central.network.session.handler.builtin.EndRequestHandler(),
    "WHITELIST_CREATE" to net.zhuruoling.omms.central.network.session.handler.builtin.whitelist.CreateWhitelistRequestHandler(),
    "CONTROLLER_GET" to GetControllerRequestHandler(),
    "WHITELIST_GET" to net.zhuruoling.omms.central.network.session.handler.builtin.whitelist.GetWhitelistRequestHandler(),
    "SYSTEM_GET_INFO" to net.zhuruoling.omms.central.network.session.handler.builtin.system.GetSysinfoRequestHandler(),
    "PERMISSION_LIST" to net.zhuruoling.omms.central.network.session.handler.builtin.permission.ListPermissionRequestHandler(),
    "CONTROLLER_LAUNCH_CONSOLE" to net.zhuruoling.omms.central.network.session.handler.builtin.controller.LaunchControllerConsoleRequestHandler(),
    "CONTROLLER_END_CONSOLE" to net.zhuruoling.omms.central.network.session.handler.builtin.controller.EndControllerConsoleRequestHandler(),
    "CONTROLLER_INPUT_CONSOLE" to net.zhuruoling.omms.central.network.session.handler.builtin.controller.SendControllerConsoleInputRequestHandler()
)
