package icu.takeneko.omms.central.network.session.handler.builtin.controller

import icu.takeneko.omms.central.controller.console.input.SessionInputSource
import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.FailureReasons
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.permission.Permission

class SendControllerConsoleInputRequestHandler : BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response { //NOTE: may ignore by client
        val id = request.getContent("consoleId")
        if (session.controllerConsoleMap.containsKey(id)) {
            val console = session.controllerConsoleMap[id]
            val line = request.getContent("command")
            val inputSource = console!!.inputSource as SessionInputSource
            inputSource.put(line)
            return request.success()
        } else {
            return request.fail(FailureReasons.CONTROLLER_NOT_FOUND)
        }
    }

    override fun requiresPermission(): Permission {
        return Permission.CONTROLLER_CONTROL
    }
}
