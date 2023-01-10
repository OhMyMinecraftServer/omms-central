package net.zhuruoling.omms.central.network.session.handler.builtin.controller;

import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.util.Result;

public class ExecuteControllersCommandRequestHandler extends BuiltinRequestHandler {
    @Override
    public Response handle(Request request, HandlerSession session) {
        Response response = new Response();
        var name = request.getContent("controller");
        var command  = request.getContent("command");
        var controller = ControllerManager.INSTANCE.getControllerByName(name);
        if (controller == null){
            return response.withResponseCode(Result.CONTROLLER_NOT_EXIST);
        }
        ControllerManager.INSTANCE.sendInstruction(controller,command);
        response.withResponseCode(Result.OK);
        return null;
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}
