package net.zhuruoling.omms.central.controller

import net.zhuruoling.omms.central.util.Util

class ControllerBindingConfig {
    val bindId = Util.generateRandomString(16)
    val controllers = mutableMapOf<String, ControllerImpl>()
    var controllerForName = ""
    var controllerForCommandExec = ""
    var controllerForMCDRCommandExec = ""
    var controllerForStatus = ""
    var controllerForConsole = ""

    fun controller(impl: ControllerImpl) {
        controllers += impl.name to impl
        if (controllerForName.isEmpty()) {
            controllerForName = impl.name
        }
        if (controllerForCommandExec.isEmpty()) {
            controllerForCommandExec = impl.name
        }
        if (controllerForMCDRCommandExec.isEmpty()) {
            controllerForMCDRCommandExec = impl.name
        }
        if (controllerForStatus.isEmpty()) {
            controllerForStatus = impl.name
        }
        if (controllerForConsole.isEmpty()) {
            controllerForConsole = impl.name
        }
    }

    fun forName(impl: ControllerImpl) {
        this.controllerForName = impl.name
    }

    fun forName(s: String) {
        this.controllerForName = s
    }

    fun forName(provider: () -> ControllerImpl) {
        this.controllerForName = provider().name
    }

    fun forCommandExec(impl: ControllerImpl) {
        this.controllerForCommandExec = impl.name
    }

    fun forCommandExec(s: String) {
        this.controllerForCommandExec = s
    }

    fun forCommandExec(provider: () -> ControllerImpl) {
        this.controllerForCommandExec = provider().name
    }

    fun forMCDRCommandExec(impl: ControllerImpl) {
        this.controllerForMCDRCommandExec = impl.name
    }

    fun forMCDRCommandExec(s: String) {
        this.controllerForMCDRCommandExec = s
    }

    fun forMCDRCommandExec(provider: () -> ControllerImpl) {
        this.controllerForMCDRCommandExec = provider().name
    }

    fun forStatus(impl: ControllerImpl) {
        this.controllerForStatus = impl.name
    }

    fun forStatus(s: String) {
        this.controllerForStatus = s
    }

    fun forStatus(provider: () -> ControllerImpl) {
        this.controllerForStatus = provider().name
    }

    fun forConsole(impl: ControllerImpl) {
        this.controllerForConsole = impl.name
    }

    fun forConsole(s: String) {
        this.controllerForConsole = s
    }

    fun forConsole(provider: () -> ControllerImpl) {
        this.controllerForConsole = provider().name
    }
}