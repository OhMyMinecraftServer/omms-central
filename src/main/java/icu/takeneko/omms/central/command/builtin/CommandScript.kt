package icu.takeneko.omms.central.command.builtin

import icu.takeneko.omms.central.command.LiteralCommand
import icu.takeneko.omms.central.command.execute
import icu.takeneko.omms.central.command.literal
import icu.takeneko.omms.central.script.ScriptManager

val commandScript = LiteralCommand("script"){
    literal("reload") {
        execute {
            0
        }
    }
    literal("list"){
        execute {
            1
        }
    }
}