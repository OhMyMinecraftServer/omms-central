package icu.takeneko.omms.central.network.session.request

import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.network.session.response.Status
import icu.takeneko.omms.central.permission.Permission

@kotlinx.serialization.Serializable
data class LoginRequest(
    val version: Long,
    val token: String
)

@kotlinx.serialization.Serializable
data class Request(
    val request: String,
    val content: Map<String, String> = mapOf(),
    val requestId: String
) {

    operator fun get(key: String): String {
        return content[key] ?: throw IllegalArgumentException("Unknown key $key in $content")
    }

    fun getContent(key: String): String {
        return content[key] ?: throw IllegalArgumentException("Unknown key $key in $content")
    }

    operator fun contains(key: String): Boolean {
        return content.containsKey(key)
    }

    fun success(): Response = Response(requestId, Status.SUCCESS, mutableMapOf())

    fun fail(reason: String? = null): Response = Response(
        requestId,
        Status.SUCCESS,
        if (reason == null)
            mutableMapOf()
        else
            mutableMapOf("reason" to reason)
    )

    fun disconnect(): Response = Response(
        requestId,
        Status.DISCONNECT,
        mutableMapOf()
    )

    fun permissionDenied(permission: Permission) = Response(
        requestId,
        Status.PERMISSION_DENIED,
        mutableMapOf()
    ).withContentPair("permission", permission)

}