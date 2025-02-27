package icu.takeneko.omms.central.identity

import icu.takeneko.omms.central.foundation.Manager
import icu.takeneko.omms.central.util.Util
import io.ktor.util.*
import kotlin.io.path.*

object IdentityProvider : Manager() {

    private val dataPath = Util.absolutePath("identity.json")
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
        sha1("${identifier.serialNumber} ${identifier.hardwareUuid} $salt".encodeToByteArray()).encodeBase64()


    data class IdentityStorage(val salt: String, val banned: MutableMap<String, SystemIdentifier>)

}