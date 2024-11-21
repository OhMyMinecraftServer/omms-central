package icu.takeneko.omms.central.network.session.handler.builtin.controller

import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.permission.Permission

class ListControllerFileRequestHandler : BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response? {
        return null
    }

    override fun requiresPermission(): Permission? {
        return null
    }
}
