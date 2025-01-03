package icu.takeneko.omms.central.network.session.handler.builtin.chatbridge

import icu.takeneko.omms.central.network.chatbridge.Broadcast
import icu.takeneko.omms.central.network.chatbridge.send
import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.permission.Permission

object SendBroadcastRequestHandler: BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        val channel = if ("channel" in request) request["channel"] else "GLOBAL"
        val message = request["message"]
        val broadcast = Broadcast("GLOBAL", message)
        broadcast.send()
        return request.success().apply {
            this["channel"] = channel
            this["message"] = message
        }
    }

    override fun requiresPermission(): Permission? = null
}


