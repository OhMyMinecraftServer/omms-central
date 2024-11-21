package icu.takeneko.omms.central.network.session.handler.builtin.controller

import icu.takeneko.omms.central.controller.ControllerManager.controllers
import icu.takeneko.omms.central.controller.ControllerManager.getControllerStatus
import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.permission.Permission
import icu.takeneko.omms.central.util.Util

class GetControllerStatusRequestHandler : BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        val controllerId = request.getContent("id")
        if (controllers.containsKey(controllerId)) {
            val status = getControllerStatus(mutableListOf(controllerId))
            return request.success().withContentPair(
                "status", Util.toJson(
                    status[controllerId]!!
                )
            )
        } else {
            return request.fail().withContentPair("controllerId", controllerId)
        }
    }

    override fun requiresPermission(): Permission? {
        return null
    }
}
