package net.zhuruoling.omms.central.network.session.handler.builtin.system;

import net.zhuruoling.omms.central.network.old.session.SessionContext;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.system.runner.RunnerManager;
import net.zhuruoling.omms.central.util.Util;
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
