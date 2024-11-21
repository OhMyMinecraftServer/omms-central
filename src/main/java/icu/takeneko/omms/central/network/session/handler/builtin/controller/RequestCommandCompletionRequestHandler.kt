package icu.takeneko.omms.central.network.session.handler.builtin.controller

import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.SkippedException
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.FailureReasons
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.permission.Permission

class RequestCommandCompletionRequestHandler : BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        val id = request.getContent("consoleId")
        if (session.controllerConsoleMap.containsKey(id)) {
            val console = session.controllerConsoleMap[id]
            val line = request.getContent("input")
            val cursorPosition = request.getContent("cursor")!!.toInt()
            console!!.complete(line, cursorPosition)
                .thenAccept { it: List<String> ->
                    session.server.sendCompletionResult(
                        it,
                        request
                    )
                }
            throw SkippedException()
        } else {
            return request.fail(FailureReasons.CONTROLLER_NOT_FOUND)
        }
    }

    override fun requiresPermission(): Permission? {
        return null
    }
}
