package icu.takeneko.omms.central.network.session.request

@kotlinx.serialization.Serializable
data class LoginRequest(
    val version: Long,
    val token:String
)

@kotlinx.serialization.Serializable
data class Request(
    val request:String,
    val content:Map<String, String> = mapOf()
){
    fun getContent(key: String): String? {
        return content[key]
    }

    fun containsKey(key: String): Boolean {
        return content.containsKey(key)
    }
}