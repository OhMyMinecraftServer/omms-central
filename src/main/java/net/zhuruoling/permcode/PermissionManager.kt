package net.zhuruoling.permcode

import com.google.gson.GsonBuilder
import net.zhuruoling.util.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.log
import kotlin.random.Random

object PermissionManager {
    var permissionTable :HashMap<Int, List<Permission>> = java.util.HashMap()
    val logger: Logger = LoggerFactory.getLogger("Main")

    data class Perm(
        val permissions: List<PermComponents>
    )

    data class PermComponents(
        val code: Int,
        val permission: Int
    )

    fun init(): Unit {
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
            Files.writeString(Path.of(Util.joinFilePaths("permissions.json")),"{\"permissions\":[]}")
            val code = Random(System.nanoTime()).nextInt(100000,999999)
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
        jsonContent = jsonContent.replace(" ","")
        val perm = gson.fromJson(jsonContent, Perm::class.javaObjectType)
        val components = perm.permissions
        components.forEach {
            permissionTable[it.code] = readPermFromInt(it)
        }
        logger.info("Permissions configured in permissions.json:")
        permissionTable.forEach {
            logger.info("${it.key} -> ${it.value}")
        }
    }

    private fun readPermFromInt(components: PermComponents): List<Permission> {
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
        var code : Int = components.permission
        var list :ArrayList<Permission> = ArrayList()
        //HIGHEST perm : 131071
        for (i in 1..16){
            val m = 1 shl i-1
            val x = code and m
            var p: Permission? = null
            when (i) {
                1 -> {
                    p = Permission.SERVER_OS_CONTROL
                }
                2 -> {
                    p = Permission.CENTRAL_SERVER_CONFIG
                }
                3 -> {
                    p = Permission.PERMISSION_LIST
                }
                4 -> {
                    p = Permission.PERMISSION_MODIFY
                }

                5 -> {
                    p = Permission.RUN_MCDR_COMMAND
                }
                6 -> {
                    p = Permission.RUN_MINECRAFT_COMMAND
                }
                7 -> {
                    p = Permission.START_SERVER
                }
                8 -> {
                    p = Permission.STOP_SERVER
                }

                9 -> {
                    p = Permission.WHITELIST_ADD
                }
                10 -> {
                    p = Permission.WHITELIST_REMOVE
                }
                11 -> {
                    p = Permission.WHITELIST_CREATE
                }
                12 -> {
                    p = Permission.WHITELIST_DELETE
                }

                13 -> {
                    p = Permission.ANNOUNCEMENT_CREATE
                }
                14 -> {
                    p = Permission.ANNOUNCEMENT_DELETE
                }
                15 -> {
                    p = Permission.ANNOUNCEMENT_EDIT
                }
                16 -> {
                    p = Permission.EXECUTE_PLUGIN_COMMAND
                }
            }
            if (x == 0){
                continue
            }
            else{
                if (p == null)
                    continue
                p.let { list.add(it) }
            }
        }
        if (list.isEmpty()){
            throw java.lang.RuntimeException("Empty permission table at $components")
        }
        return list
    }


/*
lower bits
group_server:
    server_os_control
    omms_configuration
    none
    none
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
    none
higher bits
16371: owner
 */

    fun reload(): Unit {
        logger.info("Reloading Permissions.")
        init()
    }

    fun getPermission(code: Int): List<Permission?>? {
        if (permissionTable.contains(code)){
            return permissionTable[code]
        }
        return null
    }
}