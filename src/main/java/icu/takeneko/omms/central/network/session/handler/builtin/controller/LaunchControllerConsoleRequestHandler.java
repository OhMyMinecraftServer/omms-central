package icu.takeneko.omms.central.network.session.handler.builtin.controller;

import icu.takeneko.omms.central.controller.Controller;
import icu.takeneko.omms.central.controller.ControllerManager;
import icu.takeneko.omms.central.controller.console.ControllerConsole;
import icu.takeneko.omms.central.controller.console.input.SessionInputSource;
import icu.takeneko.omms.central.controller.console.output.EncryptedSocketPrintTarget;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class LaunchControllerConsoleRequestHandler extends BuiltinRequestHandler {
    @Override
    public @Nullable Response handle(@NotNull Request request, @NotNull SessionContext session) {
        String controllerName = request.getContent("controller");
        Controller controller = ControllerManager.INSTANCE.getControllerByName(controllerName);
        if (controller == null) {
            return new Response().withResponseCode(Result.CONTROLLER_NOT_EXIST).withContentPair("controllerId", controllerName);
        }
        String id = Util.generateRandomString(16);
        ControllerConsole controllerConsoleImpl = controller.startControllerConsole(new SessionInputSource(), new EncryptedSocketPrintTarget(session.getServer()), id);
        controllerConsoleImpl.start();
        session.getControllerConsoleMap().put(id, controllerConsoleImpl);
        var consoleAlreadyStarted = new AtomicBoolean(false);
        session.getControllerConsoleMap().forEach(((s, console) -> {
            if (Objects.equals(console.getController().getName(), controllerName)) {
                consoleAlreadyStarted.set(false);
            }
        }));
        if (consoleAlreadyStarted.get()) {
            return new Response().withResponseCode(Result.CONSOLE_ALREADY_EXISTS).withContentPair("controller", controllerName);
        }
        return new Response().withResponseCode(Result.CONSOLE_LAUNCHED).withContentPair("consoleId", id).withContentPair("controller", controllerName);
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return Permission.CONTROLLER_CONTROL;
    }
}
