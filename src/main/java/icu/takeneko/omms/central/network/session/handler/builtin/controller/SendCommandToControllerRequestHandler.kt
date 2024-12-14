package icu.takeneko.omms.central.network.session.handler.builtin.controller

import icu.takeneko.omms.central.controller.ControllerManager.getControllerByName
import icu.takeneko.omms.central.controller.ControllerManager.sendCommand
import icu.takeneko.omms.central.controller.RequestUnauthorisedException
import icu.takeneko.omms.central.network.http.joinToString
import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.FailureReasons
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.permission.Permission

class SendCommandToControllerRequestHandler : BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        val name = request.getContent("controller")
        val command = request.getContent("command")
        val controller = getControllerByName(name)
            ?: return request.fail(FailureReasons.CONTROLLER_NOT_FOUND)
                .withContentPair("controllerId", name)
        try {
            val result = sendCommand(controller.name, command)
            return if (result.status) {
                request.success()
                    .withContentPair("controllerId", name)
                    .withContentPair("status", result.status.toString())
                    .withContentPair("output", joinToString<String>(result.result))
            } else {
                request.success()
                    .withContentPair("controllerId", name)
                    .withContentPair("status", result.status.toString())
                    .withContentPair("output", result.exceptionMessage)
                    .withContentPair("errorDetail", result.exceptionDetail)
            }
        } catch (e: RequestUnauthorisedException) {
            return request.fail(FailureReasons.CONTROLLER_UNAUTHORISED)
                .withContentPair("controllerId", name)
        }
    }

    override fun requiresPermission(): Permission? {
        return null
    }
}
