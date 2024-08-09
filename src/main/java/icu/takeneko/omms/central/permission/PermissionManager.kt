package icu.takeneko.omms.central.permission

import com.mojang.brigadier.context.CommandContext
import icu.takeneko.omms.central.command.CommandSourceStack
import icu.takeneko.omms.central.command.sendError
import icu.takeneko.omms.central.command.sendFeedback
import icu.takeneko.omms.central.fundation.Manager
import icu.takeneko.omms.central.network.session.server.getChecksumMD5
import icu.takeneko.omms.central.util.Util
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import kotlin.io.path.*

object PermissionManager : Manager() {
    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }
    val path = Path("./permissions.json")
    var permissionTable: MutableMap<String, MutableList<Permission>> = mutableMapOf()
    val logger: Logger = LoggerFactory.getLogger("Main")
    val changes: MutableList<PermissionChange> = mutableListOf()

    @OptIn(ExperimentalSerializationApi::class)
    override fun init() {
        permissionTable.clear()
        if (!Files.exists(Util.absolutePath("permissions.json"))) {
            logger.warn("Permission configuration does not exist!")
            val temp = Util.generateRandomString(8)
            logger.info("Created permission code ($temp) and default permission file")
            permissionTable[temp] = Permission.entries.toMutableList()
            save()
            return
        }
        permissionTable += path.inputStream().use {
            json.decodeFromStream<Map<String, MutableList<Permission>>>(it)
        }
        logger.info("Permissions configured in permissions.json:")
        permissionTable.forEach {
            logger.info("${it.key} -> ${it.value}")
        }
    }


    fun save() {
        logger.info("Saving modified buffer.")
        synchronized(permissionTable) {
            path.deleteIfExists()
            path.createFile()
            path.writeText(json.encodeToString(permissionTable))
        }
    }

    private fun applyChangeToMap(change: PermissionChange) {
        if (change.name in permissionTable.keys) {
            logger.info("Applying change $change")
            when (change.operation) {
                Operation.GRANT -> permissionTable[change.name]!!.addAll(change.changes)
                Operation.DENY -> permissionTable[change.name]!!.removeIf { it in change.changes }
                Operation.DELETE -> permissionTable.remove(change.name)
                Operation.CREATE -> permissionTable[change.name] = change.changes.toMutableList()
            }
        } else {
            throw java.lang.IllegalArgumentException("Permission Code ${change.name} not exist.($change)")
        }
    }

    fun getHashedPermissionTable() = permissionTable.mapKeys { getChecksumMD5(it.key) }

    fun getPermission(code: String): List<Permission>? {
        synchronized(permissionTable) {
            if (permissionTable.contains(code)) {
                return permissionTable[code]!!
            }
            return null
        }
    }

    fun submitPermissionChanges(permissionChange: PermissionChange) {
        if (changes.contains(permissionChange)) {
            throw RuntimeException("This change operation in permissions already exists!")
        }
        changes.add(permissionChange)
    }

    fun applyChanges(src: CommandContext<CommandSourceStack>) {
        synchronized(changes) {
            val removed = mutableListOf<PermissionChange>()
            changes.forEach {
                src.sendFeedback("Applying change: \"$it\"")
                if (it.operation == Operation.CREATE) {
                    if (it.name !in permissionTable) {
                        permissionTable[it.name] = it.changes.toMutableList()
                    } else {
                        src.sendError("Permission code ${it.name} already exists.")
                    }
                    return@forEach
                }
                if (it.name in permissionTable) {
                    when (it.operation) {
                        Operation.GRANT -> permissionTable[it.name]!!.addAll(it.changes)
                        Operation.DENY -> permissionTable[it.name]!!.removeIf { i -> i in it.changes }
                        Operation.DELETE -> permissionTable.remove(it.name)
                        else -> {}
                    }
                } else {
                    src.sendError("Permission code ${it.name} not exist.")
                }

                removed += it
            }
            changes.removeAll(removed)
        }
        src.sendFeedback("Saving modified buffer")
        save()
    }

}