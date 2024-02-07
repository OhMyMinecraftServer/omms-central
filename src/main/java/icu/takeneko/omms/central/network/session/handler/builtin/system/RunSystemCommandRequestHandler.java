package icu.takeneko.omms.central.network.session.handler.builtin.system;

import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.system.runner.RunnerManager;
import org.jetbrains.annotations.NotNull;

public class RunSystemCommandRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        var command = request.getContent("command");
        var workingDir = request.getContent("workingDir");
        var runnerDescription = request.getContent("description");
        try {
            var daemon = RunnerManager.INSTANCE.createRunner(command, workingDir, runnerDescription);
            var id = daemon.getRunnerId();
            RunnerManager.INSTANCE.launchRunner(id);
            return new Response().withResponseCode(Result.RUNNER_LAUNCHED).withContentPair("runnerId", id);
        }catch (Exception e){
            return new Response().withResponseCode(Result.FAIL).withContentPair("exception",e.toString());
        }
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.SERVER_OS_CONTROL;
    }
}
