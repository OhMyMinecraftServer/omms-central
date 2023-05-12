package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.controller.Controller;
import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.controller.console.ControllerConsole;
import net.zhuruoling.omms.central.controller.console.ControllerConsoleImpl;
import net.zhuruoling.omms.central.controller.console.input.EncryptedSocketPrintTarget;
import net.zhuruoling.omms.central.controller.console.output.SessionInputSource;
import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class LaunchControllerConsoleRequestHandler extends BuiltinRequestHandler {
    @Override
    public @Nullable Response handle(Request request, SessionContext session) {
        String controllerName = request.getContent("controller");
        Controller controller = ControllerManager.INSTANCE.getControllerByName(controllerName);
        if (controller == null){
            return new Response().withResponseCode(Result.CONTROLLER_NOT_EXIST).withContentPair("controllerId", controllerName);
        }
        String id = Util.randomStringGen(16);
        ControllerConsole controllerConsoleImpl =  controller.startControllerConsole(new SessionInputSource(),new EncryptedSocketPrintTarget(session.getServer()), id);
        controllerConsoleImpl.start();
        session.getControllerConsoleMap().put(id, controllerConsoleImpl);
        var consoleAlreadyStarted = new AtomicBoolean(false);
        session.getControllerConsoleMap().forEach(((s, console) -> {
            if (Objects.equals(console.getController().getName(), controllerName)){
                consoleAlreadyStarted.set(false);
            }
        }));
        if (consoleAlreadyStarted.get()){
            return new Response().withResponseCode(Result.CONSOLE_ALREADY_EXISTS).withContentPair("controller", controllerName);
        }
        return new Response().withResponseCode(Result.CONSOLE_LAUNCHED).withContentPair("consoleId", id).withContentPair("controller", controllerName);
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return Permission.CONTROLLER_CONTROL;
    }
}
