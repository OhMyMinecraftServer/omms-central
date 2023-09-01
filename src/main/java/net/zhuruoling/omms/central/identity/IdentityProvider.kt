package net.zhuruoling.omms.central.identity

import cn.hutool.crypto.SecureUtil

object IdentityProvider {
    fun generateIdentityCode(identifier: SystemIdentifier) =
        SecureUtil.sha256("${identifier.serialNumber} ${identifier.hardwareUuid} OMMS CENTRAL IDENTITY")
}