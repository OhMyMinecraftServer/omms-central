package icu.takeneko.omms.central.network.session.handler.builtin


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
//            Class.forName("icu.takeneko.omms.central.network.session.handler.builtin.${array[1].toLowerCasePreservingASCIIRules()}.%sRequestHandler".format(reduce))
//                .getConstructor()
//                .newInstance() as RequestHandler
//        )
//    }
//    RequestManager.registerRequest("CONTROLLERS_EXECUTE",
//        SendCommandToControllerRequestHandler()
//    )

//Jetbrains 远程同乐（不是
