package icu.takeneko.omms.central.network.session.server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import icu.takeneko.omms.central.permission.Permission
import icu.takeneko.omms.central.permission.PermissionManager.getPermission
import icu.takeneko.omms.central.util.Util
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val logger = LoggerFactory.getLogger("Auth")
val gson: Gson = GsonBuilder().serializeNulls().create()

fun doAuth(remote: String): Pair<Long, List<Permission>?> {
    val authKey = String(Base64.getDecoder().decode(Base64.getDecoder().decode(remote)))
    val date = Util.getTimeCode().toLong()
    val key = authKey.toLong()
    val permCode = key xor date
    logger.debug("Got permission code: $permCode")
    return permCode to getPermission(permCode.toInt())
}

fun getTimeBasedKey(): String {
    val date = LocalDateTime.now()
    val key = date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))
    return Util.base64Encode(Util.base64Encode(key!!))
}