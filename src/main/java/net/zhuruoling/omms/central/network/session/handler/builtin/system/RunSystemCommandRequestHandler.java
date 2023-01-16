package net.zhuruoling.omms.central.network.session.handler.builtin.system;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.system.runner.RunnerManager;

public class RunSystemCommandRequestHandler extends BuiltinRequestHandler {// TODO: 2023/1/14 add to register

    @Override
    public Response handle(Request request, HandlerSession session) {
        var command = request.getContent("command");
        var workingDir = request.getContent("workingDir");
        var runnerDescription = request.getContent("description");
        try {
            var daemon = RunnerManager.INSTANCE.createRunner(command, workingDir, runnerDescription);
            var id = daemon.getRunnerId();
            RunnerManager.INSTANCE.launchRunner(id);
            return new Response().withResponseCode(Result.OK).withContentPair("runnerId", id);
        }catch (Exception e){
            return new Response().withResponseCode(Result.FAIL).withContentPair("exception",e.toString());
        }
    }

    @Override
    public Permission requiresPermission() {
        return Permission.SERVER_OS_CONTROL;
    }
}
