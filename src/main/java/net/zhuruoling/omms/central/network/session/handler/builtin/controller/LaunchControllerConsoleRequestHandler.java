package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.controller.Controller;
import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LaunchControllerConsoleRequestHandler extends BuiltinRequestHandler {
    @Override
    public @Nullable Response handle(Request request, SessionContext session) {
        String controllerName = request.getContent("controller");
        Controller controller = Objects.requireNonNull(ControllerManager.INSTANCE.getControllerByName(controllerName)).controller();
        if (controller == null){
            return new Response().withResponseCode(Result.CONTROLLER_NOT_EXIST);
        }
        String id = Util.randomStringGen(16);
        return new Response().withResponseCode(Result.OK).withContentPair("consoleId", id);
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return Permission.CONTROLLER_CONTROL;
    }
}
