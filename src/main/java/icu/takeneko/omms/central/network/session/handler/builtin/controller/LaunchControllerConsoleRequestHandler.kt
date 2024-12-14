package icu.takeneko.omms.central.network.session.handler.builtin.controller

import icu.takeneko.omms.central.controller.ControllerManager.getControllerByName
import icu.takeneko.omms.central.controller.console.ControllerConsole
import icu.takeneko.omms.central.controller.console.input.SessionInputSource
import icu.takeneko.omms.central.controller.console.output.EncryptedSocketPrintTarget
import icu.takeneko.omms.central.network.session.SessionContext
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler
import icu.takeneko.omms.central.network.session.FailureReasons
import icu.takeneko.omms.central.network.session.request.Request
import icu.takeneko.omms.central.network.session.response.Response
import icu.takeneko.omms.central.permission.Permission
import icu.takeneko.omms.central.util.Util
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.BiConsumer

class LaunchControllerConsoleRequestHandler : BuiltinRequestHandler() {
    override fun handle(request: Request, session: SessionContext): Response {
        val controllerName = request.getContent("controller")
        if (session.controllerConsoleMap.any { it.value.controller.name == controllerName }) {
            return request.fail(FailureReasons.CONSOLE_EXISTS)
                .withContentPair("controller", controllerName)
        }
        val controller = getControllerByName(controllerName)
            ?: return request.fail(FailureReasons.CONTROLLER_NOT_FOUND)
                .withContentPair("controllerId", controllerName)
        val id = Util.generateRandomString(16)
        val controllerConsoleImpl =
            controller.startControllerConsole(
                SessionInputSource::create,
                EncryptedSocketPrintTarget(session.server),
                id
            )
        controllerConsoleImpl.start()
        session.controllerConsoleMap[id] = controllerConsoleImpl
        session.controllerConsoleRequestIds[id] = request.requestId

        return request.success()
            .withContentPair("consoleId", id)
            .withContentPair("controller", controllerName)
            .withMark("launched")
    }

    override fun requiresPermission(): Permission {
        return Permission.CONTROLLER_CONTROL
    }
}
