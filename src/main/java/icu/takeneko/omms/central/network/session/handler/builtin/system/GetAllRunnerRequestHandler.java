package icu.takeneko.omms.central.network.session.handler.builtin.system;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.system.runner.RunnerManager;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.Nullable;

public class GetAllRunnerRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(Request request, SessionContext session) {
        var list = RunnerManager.INSTANCE.getAllRunnerInfo();
        if (list.isEmpty()) {
            return new Response().withResponseCode(Result.NO_RUNNER);
        }
        return new Response().withResponseCode(Result.RUNNER_LISTED).withContentPair("info", Util.toJson(list));
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return null;
    }
}
