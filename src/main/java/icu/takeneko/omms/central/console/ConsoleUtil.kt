package icu.takeneko.omms.central.console

import io.ktor.utils.io.core.*
import icu.takeneko.omms.central.command.CommandSourceStack
import icu.takeneko.omms.central.controller.Status

fun printControllerStatus(sourceStack: CommandSourceStack, controllerId: String, status: Status) {
    ("Controller $controllerId\n" +
            "    - Queryable: ${status.isQueryable}\n" +
            "    - Running: ${status.isAlive}\n" +
            if (status.isAlive) "    - type: ${status.type}\n" +
                    "    - PlayerCount: ${status.playerCount}/${status.maxPlayerCount}\n" +
                    "    - Players: ${
                        if (status.players.isEmpty()) "[No Player]" else status.players.joinToString(
                            separator = ", ",
                            prefix = "",
                            postfix = ""
                        )
                    }"
            else "").split("\n").forEach { if (it.isNotBlank()) sourceStack.sendFeedback(it) }
}
