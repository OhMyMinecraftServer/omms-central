package icu.takeneko.omms.central.controller

import icu.takeneko.omms.central.controller.console.ControllerConsole
import icu.takeneko.omms.central.controller.console.input.InputSource
import icu.takeneko.omms.central.controller.console.output.PrintTarget

class BoundControllerImpl(val config: ControllerBindingConfig) : ControllerImpl() {
    override fun isStatusQueryable(): Boolean {
        return config.controllers[config.controllerForStatus]!!.isStatusQueryable
    }

    override fun sendCommand(command: String): CommandExecutionResult? {
        val controllerForMCDR = config.controllers[config.controllerForMCDRCommandExec]!!
        return if (command.startsWith(controllerForMCDR.mcdrCommandPrefix))
            controllerForMCDR.sendCommand(command)
        else
            config.controllers[config.controllerForCommandExec]!!.sendCommand(command)
    }

    override fun startControllerConsole(
        inputSource: InputSource,
        printTarget: PrintTarget<*, ControllerConsole>,
        id: String
    ): ControllerConsole {
        return config.controllers[config.controllerForConsole]!!.startControllerConsole(inputSource, printTarget, id)
    }

    override fun queryControllerStatus(): Status {
        return config.controllers[config.controllerForStatus]!!.queryControllerStatus()
    }

    override fun getName(): String {
        return config.controllerForName
    }

    override fun getDisplayName(): String {
        return config.controllers.values.joinToString(separator = "+"){it.displayName}
    }

    override fun getType(): String {
        return config.controllers[config.controllerForName]!!.getType()
    }

    override fun getHttpQueryAddress(): String {
        return config.controllers[config.controllerForStatus]!!.httpQueryAddress
    }
}