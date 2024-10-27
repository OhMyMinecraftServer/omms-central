package icu.takeneko.omms.central.network.session.response

@kotlinx.serialization.Serializable
data class Response(
    var responseCode: Result,
    var content: MutableMap<String, String>
) {

    constructor() : this(Result.OK, mutableMapOf())

    fun withResponseCode(code: Result): Response {
        this.responseCode = code
        return this
    }

    fun withContentPair(a: String, b: String): Response {
        content[a] = b
        return this
    }

}