package icu.takeneko.omms.central.network.session.handler.builtin.chatbridge

import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.handler.get
import icu.takeneko.omms.central.network.session.handler.plus
import icu.takeneko.omms.central.network.session.handler.set
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.network.session.response.Result
import icu.takeneko.omms.central.permission.Permission

object SetChatMessagePassthroughRequestHandler: BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        val state = (request["state"] ?: "false").toBoolean()
        session.isChatMessagePassthroughEnabled = state
        return (Response() + Result.CHAT_PASSTHROUGH_STATE_CHANGED).apply { this["state"] = state.toString() }
    }

    override fun requiresPermission(): Permission? = null
}