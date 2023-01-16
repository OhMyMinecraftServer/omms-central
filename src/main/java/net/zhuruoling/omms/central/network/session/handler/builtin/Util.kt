package net.zhuruoling.omms.central.network.session.handler.builtin

import net.zhuruoling.omms.central.network.session.handler.builtin.announcement.CreateAnnouncementRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.announcement.DeleteAnnouncementRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.announcement.GetAnnouncementRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.announcement.ListAnnouncementRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.controller.CreateControllerRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.controller.GetControllerStatusRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.controller.SendCommandToControllerRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.controller.GetControllersRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.controller.ListControllersRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.permission.*
import net.zhuruoling.omms.central.network.session.handler.builtin.system.GetAllRunnerRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.system.GetRunnerOutputRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.system.GetSysinfoRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.system.RunSystemCommandRequestHandler
import net.zhuruoling.omms.central.network.session.handler.builtin.whitelist.*
import net.zhuruoling.omms.central.network.session.request.RequestManager

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
    "CONTROLLER_GET" to net.zhuruoling.omms.central.network.session.handler.builtin.controller.GetControllersRequestHandler(),
    "WHITELIST_GET" to net.zhuruoling.omms.central.network.session.handler.builtin.whitelist.GetWhitelistRequestHandler(),
    "SYSTEM_GET_INFO" to net.zhuruoling.omms.central.network.session.handler.builtin.system.GetSysinfoRequestHandler(),
    "PERMISSION_LIST" to net.zhuruoling.omms.central.network.session.handler.builtin.permission.ListPermissionRequestHandler(),
)


fun registerBuiltinRequestHandlers() {
    builtinRequestMap.forEach {
        RequestManager.registerRequest(it.key, it.value)
    }


    //hard-coded request registering(
    //do something mojang does
    // codegen
//    RequestManager.registerRequest("WHITELIST_CREATE", CreateWhitelistRequestHandler())
//    RequestManager.registerRequest("WHITELIST_LIST", ListWhitelistRequestHandler())
//    RequestManager.registerRequest("WHITELIST_GET", GetWhitelistRequestHandler())
//    RequestManager.registerRequest("WHITELIST_ADD", AddToWhitelistRequestHandler())
//    RequestManager.registerRequest("WHITELIST_REMOVE", RemoveFromWhitelistHandler())
//    RequestManager.registerRequest("WHITELIST_DELETE", DeleteWhitelistRequestHandler())
//    RequestManager.registerRequest("PERMISSION_CREATE", CreatePermissionRequestHandler())
//    RequestManager.registerRequest("PERMISSION_DELETE", DeletePermissionRequestHandler())
//    RequestManager.registerRequest("PERMISSION_GRANT", GrantPermissionRequestHandler())
//    RequestManager.registerRequest("PERMISSION_DENY", DenyPermissionRequestHandler())
//    RequestManager.registerRequest("PERMISSION_LIST", ListPermissionRequestHandler())
//    RequestManager.registerRequest("CONTROLLER_LIST", ListControllersRequestHandler())
//    RequestManager.registerRequest("CONTROLLER_CREATE", CreateControllerRequestHandler())
//
//    RequestManager.registerRequest(
//        "CONTROLLER_EXECUTE_COMMAND",
//        SendCommandToControllerRequestHandler()
//    )
//    RequestManager.registerRequest(
//        "CONTROLLER_GET",
//        GetControllersRequestHandler()
//    )
//    RequestManager.registerRequest("CONTROLLER_GET_STATUS",GetControllerStatusRequestHandler())
//    RequestManager.registerRequest("ANNOUNCEMENT_GET", GetAnnouncementRequestHandler())
//    RequestManager.registerRequest("ANNOUNCEMENT_CREATE", CreateAnnouncementRequestHandler())
//    RequestManager.registerRequest("ANNOUNCEMENT_DELETE", DeleteAnnouncementRequestHandler())
//    RequestManager.registerRequest("ANNOUNCEMENT_LIST", ListAnnouncementRequestHandler())
//    RequestManager.registerRequest(
//        "SYSTEM_GET_INFO",
//        GetSysinfoRequestHandler()
//    )
//    RequestManager.registerRequest("SYSTEM_RUN_COMMAND", RunSystemCommandRequestHandler())
//    RequestManager.registerRequest("SYSTEM_GET_RUNNER_OUTPUT",GetRunnerOutputRequestHandler())
//    RequestManager.registerRequest("SYSTEM_GET_ALL_RUNNER", GetAllRunnerRequestHandler())
//
//    RequestManager.registerRequest("END", EndRequestHandler())

    //草
    //wjat
    // big problem
    //allright(
    // This action is not yet implemented in JetBrains Client
//    Util.BUILTIN_COMMANDS.forEach {
//        val array = it.split('_').reversed()
//        val reduce = array
//            .reduce { acc, s ->
//                acc.toLowerCasePreservingASCIIRules().replaceFirstChar { c -> c.uppercaseChar() } + s.toLowerCasePreservingASCIIRules().replaceFirstChar { c -> c.uppercaseChar() }
//            }
//        if (it == "CONTROLLERS_EXECUTE")return@forEach
//        RequestManager.registerRequest(
//            it,
//            Class.forName("net.zhuruoling.omms.central.network.session.handler.builtin.${array[1].toLowerCasePreservingASCIIRules()}.%sRequestHandler".format(reduce))
//                .getConstructor()
//                .newInstance() as RequestHandler
//        )
//    }
//    RequestManager.registerRequest("CONTROLLERS_EXECUTE",
//        SendCommandToControllerRequestHandler()
//    )
}
//Jetbrains 远程同乐（不是
