package icu.takeneko.omms.central.database

import icu.takeneko.omms.central.permission.player.Group
import icu.takeneko.omms.central.permission.player.PlayerData
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

const val createPlayerTableCommand =
    "CREATE TABLE IF NOT EXISTS `omms-player` (name text NOT NULL PRIMARY KEY,groups text NOT NULL,servers text NOT NULL);"

const val createGroupTableCommand =
    "CREATE TABLE IF NOT EXISTS `omms-group` (name text NOT NULL PRIMARY KEY,servers text NOT NULL);"

const val createCheckTableCommand =
    "CREATE TABLE IF NOT EXISTS `omms-player-check` (name text NOT NULL PRIMARY KEY,timeAt number NOT NULL,removeWhich text NOT NULL,removeType text NOT NULL);"

const val getAllGroupCommand = "SELECT * FROM \"omms-group\""

const val getAllPlayerCommand = "SELECT * FROM \"omms-player\""

object DatabaseConnection {

    private lateinit var connection: Connection
    private val logger = LoggerFactory.getLogger("DatabaseConnection")

    fun init() {
        val url = "jdbc:sqlite:omms.db"
        connection = DriverManager.getConnection(url) ?: throw IllegalArgumentException("Cannot init sqlite db.")
        execute(createPlayerTableCommand)
        execute(createGroupTableCommand)
        execute(createCheckTableCommand)
    }

    private fun execute(sql: String): Pair<Boolean, ResultSet> {
        val statement = connection.createStatement()
        logger.debug("Execute sqlite command: $sql")
        val res = statement.execute(sql)
        return res to statement.resultSet
    }

    fun getAllPlayer(): List<PlayerData> {
        val list = mutableListOf<PlayerData>()
        val result = execute(getAllPlayerCommand)
        if (result.first) {
            val resultSet = result.second
            while (resultSet.next()) {
                val servers = resultSet.getString("servers")
                val groups = resultSet.getString("groups")
                val name = resultSet.getString("name")
                val playerData = PlayerData(
                    name,
                    groups.split(", "),
                    servers.split(", ")
                )
                list.add(playerData)
            }
        }
        return list
    }

    fun getAllGroup(): List<Group> {
        val list = mutableListOf<Group>()
        val result = execute(getAllGroupCommand)
        if (result.first) {
            val resultSet = result.second
            while (resultSet.next()) {
                val servers = resultSet.getString("servers")
                val name = resultSet.getString("name")
                val group =
                    Group(name, servers.split(", "))
                list.add(group)
            }
        }
        return list
    }

    fun createGroup(name: String, servers: MutableList<String>): Boolean {
        val command = "INSERT INTO `omms-group` VALUES ('$name','${servers.joinToString(separator = ", ")}');"
        return execute(command).first
    }

    fun removeServerFromPlayer() {

    }

    fun addServerToPlayer() {

    }

    fun addServerToGroup() {

    }

    fun removeServerFromGroup() {

    }

    fun checkPlayerExists(player: String): Boolean {
        val command = "select * from \"omms-player\" where name = '$player'"
        val result = execute(command).second
        return result.next()
    }

    fun checkGroupExists(group: String): Boolean {
        val command = "select * from \"omms-player\" where name = '$group'"
        val result = execute(command).second
        return result.next()
    }


    fun createPlayer(
        name: String,
        servers: MutableList<String> = mutableListOf(),
        groups: List<String> = mutableListOf()
    ): Boolean {
        val command = "INSERT INTO `omms-player` VALUES ('$name','${servers.joinToString(separator = ", ")}','${
            groups.joinToString(separator = ", ")
        }');"
        return execute(command).first
    }

    fun deletePlayer(name: String): Boolean {
        val command = "DELETE FROM \"omms-player\" WHERE name = '$name'"
        return execute(command).first
    }

    fun close() {
        connection.close()
    }
}