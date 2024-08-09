package icu.takeneko.omms.central.network.session.server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import icu.takeneko.omms.central.permission.Permission
import icu.takeneko.omms.central.permission.PermissionManager
import icu.takeneko.omms.central.util.Util
import org.slf4j.LoggerFactory
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val logger = LoggerFactory.getLogger("Auth")
val gson: Gson = GsonBuilder().serializeNulls().create()

fun doAuth(remote: String): Pair<String, List<Permission>>? {
    val s = getHashedCode(remote)
    if (s in PermissionManager.getHashedPermissionTable()){
        return s to PermissionManager.getHashedPermissionTable()[s]!!
    }
    return null
}

fun getHashedCode(encoded: String): String {
    val date = LocalDateTime.now()
    val time = date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))
    val tok = String(Base64.getDecoder().decode(encoded)).split(";".toRegex()).dropLastWhile { it.isEmpty() }
        .toTypedArray()
    require(tok.size == 2) { "Invalid token: expect \";\"" }
    val t = tok[0]
    val hashed = tok[1]
    require(t == time) { "Invalid token: time mismatch, expect $time, got $t" }
    return hashed
}

fun getChecksumMD5(original: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return Base64.getEncoder().encodeToString(digest.digest(original.toByteArray()))
}

fun getTimeBasedKey(): String {
    val date = LocalDateTime.now()
    val key = date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))
    return Util.base64Encode(Util.base64Encode(key!!))
}