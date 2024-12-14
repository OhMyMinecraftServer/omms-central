package icu.takeneko.omms.central.network.session.response

import icu.takeneko.omms.central.network.session.FailureReasons
import icu.takeneko.omms.central.util.Util

@kotlinx.serialization.Serializable
data class Response(
    val requestId: String,
    var event: Status,
    var content: MutableMap<String, String>
) {

    fun withContentPair(a: String, b: String): Response {
        content[a] = b
        return this
    }

    fun withMark(m: String): Response {
        content["marker_$m"] = ""
        return this;
    }

    fun withFailureReason(b: String): Response {
        content["reason"] = b
        return this
    }


    fun withContentPair(a: String, b: Any): Response = withContentPair(a, Util.toJson(b))

    operator fun set(key: String, value: String) {
        withContentPair(key, value)
    }

    operator fun contains(key: String): Boolean {
        return this.content.containsKey(key)
    }

    companion object {
        fun rateExceeded() = Response("", Status.FAIL, mutableMapOf("reason" to FailureReasons.RATE_EXCEED))
    }

}