package net.zhuruoling.network.session.handler;

import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.message.MessageBuilderKt;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.plugin.PluginManager;
import net.zhuruoling.plugin.RequestServerInterface;
import net.zhuruoling.util.Result;

import java.util.Objects;
import java.util.function.BiFunction;

public class PluginRequestHandler extends RequestHandler {
    private final String pluginName;
    private final String code;
    private final String funcName;
    private final BiFunction<RequestServerInterface, Request, Response> function;

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
    public Response handle(Request request, HandlerSession session) {
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
    public Permission requiresPermission() {
        return Permission.EXECUTE_PLUGIN_REQUEST;
    }
}
