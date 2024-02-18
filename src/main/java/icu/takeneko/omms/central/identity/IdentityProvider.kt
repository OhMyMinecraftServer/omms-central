package icu.takeneko.omms.central.identity

import cn.hutool.crypto.SecureUtil
import icu.takeneko.omms.central.util.Manager
import icu.takeneko.omms.central.util.Util
import kotlin.io.path.*

object IdentityProvider : Manager() {

    private val dataPath = Path(Util.joinFilePaths("identity.json"))
    private lateinit var salt: String
    private lateinit var banned: MutableMap<String, SystemIdentifier>
    override fun init() {
        if (!dataPath.exists()) {
            dataPath.deleteIfExists()
            dataPath.createFile()
            dataPath.writer().use {
                it.write(Util.toJson(IdentityStorage("OMMS CENTRAL IDENTITY", mutableMapOf())))
            }
        }
        val identityStorage = dataPath.reader().use {
            Util.gson.fromJson(it, IdentityStorage::class.java)
        }
        salt = identityStorage.salt
        banned = identityStorage.banned
    }

    fun save() {
        val storage = IdentityStorage(salt, banned)
        dataPath.deleteIfExists()
        dataPath.createFile()
        dataPath.writer().use {
            it.write(Util.toJson(storage))
        }
    }

    fun checkIfBanned(identifier: SystemIdentifier) =
        banned.any { it.value.anyEquals(identifier) || it.key == generateIdentityCode(identifier) }

    fun recalculateAllIdentityCode() {
        this.banned = banned.values.associateBy { generateIdentityCode(it) }.toMutableMap()
    }

    fun generateIdentityCode(identifier: SystemIdentifier): String =
        SecureUtil.sha256("${identifier.serialNumber} ${identifier.hardwareUuid} $salt")


    data class IdentityStorage(val salt: String, val banned: MutableMap<String, SystemIdentifier>)

}