package icu.takeneko.omms.central.network.session.handler.builtin.chatbridge

import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.permission.Permission

object SetChatMessagePassthroughRequestHandler: BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        val state = request["state"].toBoolean()
        session.isChatMessagePassthroughEnabled = state
        return request.success().apply { this["state"] = state.toString() }
    }

    override fun requiresPermission(): Permission? = null
}