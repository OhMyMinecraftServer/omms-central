package net.zhuruoling.permission

import com.google.gson.GsonBuilder
import net.zhuruoling.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.random.Random

object PermissionManager {
    var permissionTable: HashMap<Int, List<Permission>> = java.util.HashMap()
    val logger: Logger = LoggerFactory.getLogger("Main")
    var tableHash: Int = 0;
    val changesTable: MutableList<PermissionChange> = mutableListOf()

    data class Perm(
        val permissions: List<PermComponents>
    )

    data class PermComponents(
        val code: Int,
        val permission: Int
    )

    fun init() {
        permissionTable.clear()
        if (!Files.exists(Path.of(Util.joinFilePaths("permissions.json")))) {
            logger.warn("Permission File does not exist!")
            Files.createFile(
                Path.of(
                    Util.joinFilePaths(
                        "permissions.json"
                    )
                )
            )
            logger.info("Creating empty permission file.")
            Files.writeString(Path.of(Util.joinFilePaths("permissions.json")), "{\"permissions\":[]}")
            val code = Random(System.nanoTime()).nextInt(100000, 999999)
            logger.info("Created temporary permission code: $code,this code has got super cow power and it is available to use until the next time omms startup.")
            permissionTable[code] = Permission.values().toList()
            return
        }
        val stringMutableList = Files.readAllLines(Path.of(Util.joinFilePaths("permissions.json")))
        var jsonContent = ""
        stringMutableList.forEach {
            jsonContent += it
        }
        val gson = GsonBuilder().serializeNulls().create()
        jsonContent = jsonContent.replace(" ", "")
        val perm = gson.fromJson(jsonContent, Perm::class.javaObjectType)
        val components = perm.permissions
        components.forEach {
            permissionTable[it.code] = readPermFromInt(it)
        }
        logger.info("Permissions configured in permissions.json:")
        permissionTable.forEach {
            logger.info("${it.key} -> ${it.value}")
        }
        tableHash = permissionTable.hashCode()
    }


    fun readPermFromInt(components: PermComponents): List<Permission> {
        /*
        def main():
        code :int = int(input("code>"))
        print(code)
        print(code.bit_count())
        table = []
        for i in range(1,code.bit_count()+2,1):
        m = 1 << i-1A
        print(f"n={i},m={m}")
        x = code & m
        if x == 0:
            table.append(0)
        else:
            table.append(1)
        table.reverse()
        print(table)
         */
        var code: Int = components.permission
        var list: ArrayList<Permission> = ArrayList()
        //HIGHEST perm : 131071
        for (i in 1..16) {
            val m = 1 shl i - 1
            val x = code and m
            var p: Permission? = null
            when (i) {
                1 -> {
                    p = Permission.SERVER_OS_CONTROL
                }

                2 -> {
                    p = Permission.CENTRAL_SERVER_CONTROL
                }

                3 -> {
                    p = Permission.PERMISSION_LIST
                }

                4 -> {
                    p = Permission.PERMISSION_MODIFY
                }

                5 -> {
                    p = Permission.CONTROLLER_GET
                }

                6 -> {
                    p = Permission.CONTROLLER_EXECUTE
                }

                7 -> {
                    p = Permission.CONTROLLER_MODIFY
                }

                8 -> {
                    p = Permission.WHITELIST_ADD
                }

                9 -> {
                    p = Permission.WHITELIST_REMOVE
                }

                10 -> {
                    p = Permission.WHITELIST_CREATE
                }

                11 -> {
                    p = Permission.WHITELIST_DELETE
                }

                12 -> {
                    p = Permission.ANNOUNCEMENT_READ
                }

                13 -> {
                    p = Permission.ANNOUNCEMENT_CREATE
                }

                14 -> {
                    p = Permission.ANNOUNCEMENT_DELETE
                }

                15 -> {
                    p = Permission.ANNOUNCEMENT_MODIFY
                }

                16 -> {
                    p = Permission.EXECUTE_PLUGIN_REQUEST
                }
            }
            if (x == 0) {
                continue
            } else {
                p.let {
                    if (it != null) {
                        list.add(it)
                    }
                }
            }
        }
        if (list.isEmpty()) {
            throw java.lang.RuntimeException("Empty permission table at $components")
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

                Permission.CONTROLLER_GET -> {
                    code += 1 shl 4
                }

                Permission.CONTROLLER_EXECUTE -> {
                    code += 1 shl 5
                }

                Permission.CONTROLLER_MODIFY -> {
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

                Permission.ANNOUNCEMENT_READ -> {
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

    private fun makePermComponents(code: Int, permissions: List<Permission>): PermComponents {
        return PermComponents(code, calcPermission(permissions))
    }

    fun savePermissionFile() {
        logger.info("Saving modified buffer.")
        val list: MutableList<PermComponents> = mutableListOf()
        permissionTable.forEach {
            list.apply {
                list.add(makePermComponents(it.key, it.value))
            }
        }
        val perm: Perm = Perm(list)
        changesTable.removeIf {
            changesTable.contains(it)
        }
    }


    fun getPermission(code: Int): List<Permission>? {
        if (permissionTable.contains(code)) {
            return permissionTable[code]
        }
        return null
    }


    fun submitPermissionChanges(permissionChange: PermissionChange) {
        if (changesTable.contains(permissionChange)) {
            throw RuntimeException("This change operation in permissions already exists!")
        }
        changesTable.add(permissionChange)
    }

    @JvmStatic
    fun getPermissionsFromString(string: String): List<Permission>? {
        return if (string.contains(" ")) {
            val permissionNames = Arrays.stream(string.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()).toList()
            val arrayList = ArrayList<Permission>()
            permissionNames.forEach{
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


}