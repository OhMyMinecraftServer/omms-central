package icu.takeneko.omms.central.network.session.handler.builtin.controller

import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.permission.Permission

class EndControllerConsoleRequestHandler : BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        val id = request.getContent("consoleId")
        if (session.controllerConsoleMap.containsKey(id)) {
            val console = session.controllerConsoleMap[id]
            console!!.close()
            session.controllerConsoleMap.remove(id)
            return request.success().withContentPair("consoleId", id)
        } else {
            return request.fail().withContentPair("consoleId", id)
        }
    }

    override fun requiresPermission(): Permission {
        return Permission.CONTROLLER_CONTROL
    }
}
