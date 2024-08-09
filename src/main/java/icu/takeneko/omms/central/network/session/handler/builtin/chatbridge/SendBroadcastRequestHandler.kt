package icu.takeneko.omms.central.network.session.handler.builtin.chatbridge

import icu.takeneko.omms.central.command.builtin.broadcastCommand
import icu.takeneko.omms.central.network.chatbridge.buildBroadcast
import icu.takeneko.omms.central.network.chatbridge.sendBroadcast
import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.handler.contains
import icu.takeneko.omms.central.network.session.handler.get
import icu.takeneko.omms.central.network.session.handler.plus
import icu.takeneko.omms.central.network.session.handler.set
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.network.session.response.Result
import icu.takeneko.omms.central.permission.Permission

object SendBroadcastRequestHandler: BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        val channel = if ("channel" in request) request["channel"]!! else "GLOBAL"
        val message = request["message"] ?: return Response() + Result.INVALID_ARGUMENTS
        val broadcast = buildBroadcast("GLOBAL", message)
        sendBroadcast(broadcast)
        return (Response() + Result.BROADCAST_SENT).apply {
            this["channel"] = channel
            this["message"] = message
        }
    }

    override fun requiresPermission(): Permission? = null
}


