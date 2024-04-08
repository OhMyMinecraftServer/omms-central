package icu.takeneko.omms.central.permission

import com.google.gson.GsonBuilder
import com.mojang.brigadier.context.CommandContext
import icu.takeneko.omms.central.command.CommandSourceStack
import icu.takeneko.omms.central.command.sendError
import icu.takeneko.omms.central.command.sendFeedback
import icu.takeneko.omms.central.util.Manager
import icu.takeneko.omms.central.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.random.Random

object PermissionManager : Manager() {
    var permissionTable: HashMap<Int, MutableList<Permission>> = hashMapOf()
    val logger: Logger = LoggerFactory.getLogger("Main")
    val changes: MutableList<PermissionChange> = mutableListOf()

    data class PermissionStorage(
        val permissions: List<PermissionEntry>
    )

    data class PermissionEntry(
        val code: Int,
        val permission: Int
    )

    override fun init() {
        permissionTable.clear()
        if (!Files.exists(Util.absolutePath("permissions.json"))) {
            logger.warn("Permission File does not exist!")
            Util.fileOf("permissions.json").createNewFile()
            logger.info("Creating empty permission file.")
            Files.writeString(Util.absolutePath("permissions.json"), "{\"permissions\":[]}")
            val code = Random(System.nanoTime()).nextInt(100000, 999999)
            logger.info("Created temporary permission code: $code,this code has got super cow power and it is available to use until the next time omms startup.")
            permissionTable[code] = Permission.entries.toMutableList()
            return
        }
        val reader = FileReader(Util.absolutePath("permissions.json").toFile())
        val gson = GsonBuilder().serializeNulls().create()
        val perm = gson.fromJson(reader, PermissionStorage::class.javaObjectType)
        reader.close()
        val components = perm.permissions
        components.forEach {
            permissionTable[it.code] = readPermFromInt(it)
        }
        logger.info("Permissions configured in permissions.json:")
        permissionTable.forEach {
            logger.info("${it.key} -> ${it.value}")
        }
    }


    fun readPermFromInt(components: PermissionEntry): MutableList<Permission> {
        val code: Int = components.permission
        val list: ArrayList<Permission> = ArrayList()
        for (i in 1..16) {
            val m = 1 shl i - 1
            val x = code and m
            val p = when (i) {
                1 -> {
                     Permission.SERVER_OS_CONTROL
                }

                2 -> {
                     Permission.CENTRAL_SERVER_CONTROL
                }

                3 -> {
                     Permission.PERMISSION_LIST
                }

                4 -> {
                     Permission.PERMISSION_MODIFY
                }

                5 -> {
                     Permission.RESERVED_2
                }

                6 -> {
                     Permission.CONTROLLER_CONTROL
                }

                7 -> {
                     Permission.CONTROLLER_CREATE
                }

                8 -> {
                     Permission.WHITELIST_ADD
                }

                9 -> {
                     Permission.WHITELIST_REMOVE
                }

                10 -> {
                     Permission.WHITELIST_CREATE
                }

                11 -> {
                     Permission.WHITELIST_DELETE
                }

                12 -> {
                     Permission.RESERVED_1
                }

                13 -> {
                     Permission.ANNOUNCEMENT_CREATE
                }

                14 -> {
                     Permission.ANNOUNCEMENT_DELETE
                }

                15 -> {
                     Permission.ANNOUNCEMENT_MODIFY
                }

                16 -> {
                     Permission.EXECUTE_PLUGIN_REQUEST
                }

                else -> {
                    throw IllegalArgumentException("how")
                }
            }
            if (x == 0) {
                continue
            } else {
                list.add(p)
            }
        }
        return list
    }


    /*
    lower bits
    group_server:
        server_os_control
        omms_configuration
        PERMISSION_LIST
        PERMISSION_MODIFY
    group_minecraft_server_control:
        run_mcdr_command
        run_minecraft_command
        start_server
        stop_server
    group_whitelists:
        whitelist_add
        whitelist_remove
        whitelist_create
        whitelist_delete
    group_announcement:
        announcement_create
        announcement_delete
        announcement_edit
        EXECUTE_PLUGIN_COMMAND
    higher bits
    16371: owner
     */

    fun reload(): Unit {
        logger.info("Reloading Permissions.")
        init()
    }

    @JvmStatic
    fun calcPermission(permission: List<Permission>): Int {
        var code = 0
        val permissions = mutableListOf<Permission>()
        permission.forEach {
            if (!permissions.contains(it)) permissions.add(it)
        }
        permissions.forEach {
            when (it) {
                Permission.SERVER_OS_CONTROL -> {
                    code += 1 shl 0
                }

                Permission.CENTRAL_SERVER_CONTROL -> {
                    code += 1 shl 1
                }

                Permission.PERMISSION_LIST -> {
                    code += 1 shl 2
                }

                Permission.PERMISSION_MODIFY -> {
                    code += 1 shl 3
                }

                Permission.RESERVED_2 -> {
                    code += 1 shl 4
                }

                Permission.CONTROLLER_CONTROL -> {
                    code += 1 shl 5
                }

                Permission.CONTROLLER_CREATE -> {
                    code += 1 shl 6
                }

                Permission.WHITELIST_ADD -> {
                    code += 1 shl 7
                }

                Permission.WHITELIST_REMOVE -> {
                    code += 1 shl 8
                }

                Permission.WHITELIST_CREATE -> {
                    code += 1 shl 9
                }

                Permission.WHITELIST_DELETE -> {
                    code += 1 shl 10
                }

                Permission.RESERVED_1 -> {
                    code += 1 shl 11
                }

                Permission.ANNOUNCEMENT_CREATE -> {
                    code += 1 shl 12
                }

                Permission.ANNOUNCEMENT_DELETE -> {
                    code += 1 shl 13
                }

                Permission.ANNOUNCEMENT_MODIFY -> {
                    code += 1 shl 14
                }

                Permission.EXECUTE_PLUGIN_REQUEST -> {
                    code += 1 shl 15
                }
            }
        }
        return code
    }

    private fun makePermissionEntry(code: Int, permissions: List<Permission>): PermissionEntry {
        return PermissionEntry(code, calcPermission(permissions))
    }

    fun savePermissionFile() {
        logger.info("Saving modified buffer.")
        synchronized(permissionTable) {
            val perm: PermissionStorage
            synchronized(changes) {
                changes.forEach {
                    applyChangeToMap(it)
                }
                val list: MutableList<PermissionEntry> = mutableListOf()
                permissionTable.forEach {
                    list.add(makePermissionEntry(it.key, it.value))
                }
                perm = PermissionStorage(list)
                changes.clear()
                logger.info("Permissions saved in memory:")
                permissionTable.forEach {
                    logger.info("${it.key} -> ${it.value}")
                }
            }
            Files.deleteIfExists(Util.absolutePath("permissions.json"))
            Files.createFile(Util.absolutePath("permissions.json"))
            val writer = FileWriter(Util.fileOf("permissions.json"))
            writer.write(Util.toJson(perm))
            writer.close()
            reload()
        }
    }

    private fun applyChangeToMap(change: PermissionChange) {
        if (change.code in permissionTable.keys) {
            logger.info("Applying change $change")
            when (change.operation) {
                Operation.GRANT -> permissionTable[change.code]!!.addAll(change.changes)
                Operation.DENY -> permissionTable[change.code]!!.removeIf { it in change.changes }
                Operation.DELETE -> permissionTable.remove(change.code)
                Operation.CREATE -> permissionTable[change.code] = change.changes.toMutableList()
                else -> {}
            }
        } else {
            throw java.lang.IllegalArgumentException("Permission Code ${change.code} not exist.($change)")
        }
    }


    fun getPermission(code: Int): List<Permission>? {
        synchronized(permissionTable) {
            if (permissionTable.contains(code)) {
                return permissionTable[code]
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

    @JvmStatic
    fun getPermissionsFromString(string: String): List<Permission>? {
        return if (string.contains(" ")) {
            val permissionNames = Arrays.stream(string.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()).toList()
            val arrayList = ArrayList<Permission>()
            permissionNames.forEach {
                try {
                    val permission =
                        Permission.valueOf(it!!)
                    arrayList.add(permission)
                } catch (e: IllegalArgumentException) {
                    logger.error(
                        "$it is not a valid permission name",
                        IllegalPermissionNameException(it, e)
                    )
                }
            }
            arrayList
        } else {
            try {
                val permission = Permission.valueOf(string)
                listOf(permission)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    fun applyChanges(src: CommandContext<CommandSourceStack>) {
        synchronized(changes) {
            val removed = mutableListOf<PermissionChange>()
            changes.forEach {
                src.sendFeedback("Applying change: \"$it\"")
                if (it.operation == Operation.CREATE) {
                    if (it.code !in permissionTable) {
                        permissionTable[it.code] = it.changes.toMutableList()
                    } else {
                        src.sendError("Permission code ${it.code} already exists.")
                    }
                    return@forEach
                }
                if (it.code in permissionTable) {
                    when (it.operation) {
                        Operation.GRANT -> permissionTable[it.code]!!.addAll(it.changes)
                        Operation.DENY -> permissionTable[it.code]!!.removeIf { i -> i in it.changes }
                        Operation.DELETE -> permissionTable.remove(it.code)
                        else -> {}
                    }
                } else {
                    src.sendError("Permission code ${it.code} not exist.")
                }

                removed += it
            }
            changes.removeAll(removed)
        }
        src.sendFeedback("Saving modified buffer")
        synchronized(permissionTable) {
            val perm: PermissionStorage
            synchronized(changes) {
                changes.forEach {
                    applyChangeToMap(it)
                }
                val list: MutableList<PermissionEntry> = mutableListOf()
                permissionTable.forEach {
                    list.add(makePermissionEntry(it.key, it.value))
                }
                perm = PermissionStorage(list)
                changes.clear()
                src.sendFeedback("Permissions saved in memory:")
                permissionTable.forEach {
                    src.sendFeedback("${it.key} -> ${it.value}")
                }
            }
            Files.deleteIfExists(Util.absolutePath("permissions.json"))
            Files.createFile(Util.absolutePath("permissions.json"))
            val writer = FileWriter(Util.fileOf("permissions.json"))
            writer.write(Util.toJson(perm))
            writer.close()
            reload()
        }
    }

}