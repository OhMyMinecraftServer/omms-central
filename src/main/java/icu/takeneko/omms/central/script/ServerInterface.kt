package icu.takeneko.omms.central.script

import icu.takeneko.omms.central.command.CommandManager
import icu.takeneko.omms.central.command.CommandSourceStack
import jep.python.PyCallable

class ServerInterface(private val scriptId:String) {
    fun runCommand(command: String) {
        CommandManager.INSTANCE.dispatchCommand(command, CommandSourceStack(CommandSourceStack.Source.SCRIPT))
    }

    fun defineCommand(command: String, callable:PyCallable){
        val sc = ScriptCommand(command, callable)
        CommandManager.INSTANCE.registerScriptCommand(scriptId, sc)
    }
}