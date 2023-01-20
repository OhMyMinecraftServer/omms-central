package net.zhuruoling.omms.central.network.session.handler;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.message.MessageBuilderKt;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.plugin.PluginManager;
import net.zhuruoling.omms.central.plugin.RequestServerInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

public class PluginRequestHandler extends RequestHandler {
    private final String pluginName;
    private final String code;
    private final String funcName;
    private final @Nullable BiFunction<RequestServerInterface, Request, Response> function;

    public PluginRequestHandler(String pluginName, String code, String funcName) {
        super("PLUGIN %s".formatted(pluginName));
        this.pluginName = pluginName;
        this.code = code;
        this.funcName = funcName;
        function = null;
    }

    public PluginRequestHandler(String pluginName, String code, BiFunction<RequestServerInterface, Request, Response> function) {
        super("PLUGIN %s".formatted(pluginName));
        this.pluginName = pluginName;
        this.code = code;
        this.funcName = "";
        this.function = function;
    }



    public String getPluginName() {
        return pluginName;
    }

    public String getCode() {
        return code;
    }

    public String getFuncName() {
        return funcName;
    }

    @Override
    public Response handle(@NotNull Request request, @NotNull HandlerSession session) {
        if (!session.getPermissions().contains(Permission.EXECUTE_PLUGIN_REQUEST)) {
            try {
                session.getEncryptedConnector().println(MessageBuilderKt.build(Result.PERMISSION_DENIED));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (!Objects.equals(request.getRequest(), code)) {
            throw new UnsupportedOperationException("The operation code defined in this class does not align with requested operation code.");
        }
        if (function != null) {
            return function.apply(new RequestServerInterface(session, pluginName), request);
        } else {
            return (Response) PluginManager.INSTANCE.execute(pluginName, funcName, request, new RequestServerInterface(session, pluginName));
        }
    }

    @Override
    public @NotNull Permission requiresPermission() {
        return Permission.EXECUTE_PLUGIN_REQUEST;
    }
}
