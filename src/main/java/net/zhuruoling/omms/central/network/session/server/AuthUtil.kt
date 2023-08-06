package net.zhuruoling.omms.central.network.session.server

import net.zhuruoling.omms.central.permission.Permission
import net.zhuruoling.omms.central.permission.PermissionManager.getPermission
import net.zhuruoling.omms.central.util.Util
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun doAuth(remote: String): List<Permission>?{
    val authKey = String(Base64.getDecoder().decode(Base64.getDecoder().decode(remote)))
    val date = Util.getTimeCode().toLong()
    val key = authKey.toLong()
    val permCode = key xor date
    return getPermission(permCode.toInt())
}

fun getTimeBasedKey():String{
    val date = LocalDateTime.now()
    val key = date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))
    return Util.base64Encode(Util.base64Encode(key!!))
}