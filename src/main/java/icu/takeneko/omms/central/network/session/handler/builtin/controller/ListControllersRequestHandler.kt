package icu.takeneko.omms.central.network.session.handler.builtin.controller

import icu.takeneko.omms.central.controller.ControllerManager.controllers
import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.ControllerData
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.permission.Permission
import icu.takeneko.omms.central.util.Util

class ListControllersRequestHandler : BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        val controllerNames: Set<String> = controllers.keys
        val json = Util.gson.toJson(controllerNames)
        return request.success()
            .withContentPair("names", json)
            .apply {
                controllerNames.forEach {
                    withContentPair("it",ControllerData.fromController(controllers[it]!!))
                }
            }
    }

    override fun requiresPermission(): Permission? {
        return null
    }
}