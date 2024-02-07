package icu.takeneko.omms.central.network.session.handler.builtin.system;

import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.permission.Permission;
import kotlin.collections.CollectionsKt;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.system.runner.RunnerManager;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GetRunnerOutputRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(@NotNull Request request, SessionContext session) {
        var runnerId = request.getContent("runnerId");
        var outputResult = RunnerManager.INSTANCE.runIfRunnerExists(runnerId, ((runnerManager, s, runnerDaemon) -> {
            List<String> list = CollectionsKt.mutableListOf();
            if (runnerDaemon.getProcessStarted()){
                if (runnerDaemon.getProcessStarted()){
                    list.addAll(Objects.requireNonNull(runnerDaemon.getReader()).getAllLines());
                }else {
                    list.add("An exception occurred while Runner attempt to launch a process.");
                    if (runnerDaemon.getStartFailReason() != null){
                        var ex = runnerDaemon.getStartFailReason();
                        StringWriter writer = new StringWriter();
                        ex.printStackTrace(new PrintWriter(writer));
                        list.addAll(Arrays.asList(writer.toString().split("\n")));
                    }
                }
            }
            return list;
        }));
        if (outputResult != null) {
            return new Response().withResponseCode(Result.RUNNER_OUTPUT_GOT).withContentPair("output", Util.toJson(outputResult));
        }else {
            return new Response().withResponseCode(Result.RUNNER_NOT_EXIST);
        }
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.SERVER_OS_CONTROL;
    }
}