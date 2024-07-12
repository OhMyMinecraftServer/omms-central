package icu.takeneko.omms.central.script

import icu.takeneko.omms.central.command.LiteralCommand
import icu.takeneko.omms.central.command.execute
import icu.takeneko.omms.central.command.getStringArgument
import icu.takeneko.omms.central.command.greedyStringArgument
import jep.python.PyCallable

class ScriptCommand(val literal: String, val callable: PyCallable) {
    val command = LiteralCommand(literal){
        greedyStringArgument("args"){
            execute {
                ScriptManager.run {
                    callable.call(getStringArgument("args").split(" "))
                }
                0
            }
        }
    }.node
}