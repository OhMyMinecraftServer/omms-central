package icu.takeneko.omms.central.network.session.handler.builtin.chatbridge

import icu.takeneko.omms.central.network.chatbridge.ChatMessageCache
import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.handler.plus
import icu.takeneko.omms.central.network.session.handler.set
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.network.session.response.Result
import icu.takeneko.omms.central.permission.Permission

object GetChatHistoryRequestHandler: BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        return (Response() + Result.GOT_CHAT_HISTORY).apply{ this["content"] = ChatMessageCache.serialize() }
    }

    override fun requiresPermission(): Permission? = null
}