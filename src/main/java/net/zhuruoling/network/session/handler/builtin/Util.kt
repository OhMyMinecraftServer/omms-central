package net.zhuruoling.network.session.handler.builtin

import io.ktor.util.*
import net.zhuruoling.network.session.handler.RequestHandler
import net.zhuruoling.network.session.handler.builtin.controller.ExecuteControllersCommandRequestHandler
import net.zhuruoling.network.session.handler.builtin.controller.GetControllersRequestHandler
import net.zhuruoling.network.session.handler.builtin.controller.ListControllersRequestHandler
import net.zhuruoling.network.session.handler.builtin.permission.*
import net.zhuruoling.network.session.handler.builtin.system.GetSysinfoRequestHandler
import net.zhuruoling.network.session.handler.builtin.whitelist.*
import net.zhuruoling.network.session.request.RequestManager
import net.zhuruoling.util.Util


fun registerBuiltinRequestHandlers() {
    //hard-coded request registering(
    //do something mojang does
    // codegen
    RequestManager.registerRequest("WHITELIST_CREATE", CreateWhitelistRequestHandler())
    RequestManager.registerRequest("WHITELIST_LIST", ListWhitelistRequestHandler())
    RequestManager.registerRequest("WHITELIST_GET", GetWhitelistRequestHandler())
    RequestManager.registerRequest("WHITELIST_ADD", AddToWhitelistRequestHandler())
    RequestManager.registerRequest("WHITELIST_REMOVE", RemoveFromWhitelistHandler())
    RequestManager.registerRequest("WHITELIST_DELETE", DeleteWhitelistRequestHandler())
    RequestManager.registerRequest("PERMISSION_CREATE", CreatePermissionRequestHandler())
    RequestManager.registerRequest("PERMISSION_DELETE", DeletePermissionRequestHandler())
    RequestManager.registerRequest("PERMISSION_GRANT", GrantPermissionRequestHandler())
    RequestManager.registerRequest("PERMISSION_DENY", DenyPermissionRequestHandler())
    RequestManager.registerRequest("PERMISSION_LIST", ListPermissionRequestHandler())
    RequestManager.registerRequest("CONTROLLERS_LIST", ListControllersRequestHandler())
    RequestManager.registerRequest(
        "CONTROLLERS_EXECUTE",
        ExecuteControllersCommandRequestHandler()
    )
    RequestManager.registerRequest(
        "CONTROLLERS_GET",
        GetControllersRequestHandler()
    )
    RequestManager.registerRequest(
        "SYSINFO_GET",
        GetSysinfoRequestHandler()
    )
    RequestManager.registerRequest("END", EndRequestHandler())
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
//            Class.forName("net.zhuruoling.network.session.handler.builtin.${array[1].toLowerCasePreservingASCIIRules()}.%sRequestHandler".format(reduce))
//                .getConstructor()
//                .newInstance() as RequestHandler
//        )
//    }
//    RequestManager.registerRequest("CONTROLLERS_EXECUTE",
//        ExecuteControllersCommandRequestHandler()
//    )
}
//Jetbrains 远程同乐（不是
