package net.zhuruoling.handler;

import net.zhuruoling.request.Request;
import net.zhuruoling.message.MessageBuilderKt;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.plugin.PluginManager;
import net.zhuruoling.plugin.RequestServerInterface;
import net.zhuruoling.session.HandlerSession;
import net.zhuruoling.util.Result;

import java.util.Objects;

public class PluginRequestHandler extends RequestHandler {
    private final String pluginName;
    private final String code;
    private final String funcName;

    public PluginRequestHandler(String pluginName, String code, String funcName) {
        super("PLUGIN%s".formatted(pluginName));
        this.pluginName = pluginName;
        this.code = code;
        this.funcName = funcName;
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
    public void handle(Request request, HandlerSession session) {
        if (!session.getPermissions().contains(Permission.EXECUTE_PLUGIN_COMMAND)) {
            try {
                session.getEncryptedConnector().println(MessageBuilderKt.build(Result.PERMISSION_DENIED));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (!Objects.equals(request.getRequest(), code)) {
            throw new UnsupportedOperationException("The operation code defined in this class does not align with requested operation code.");
        }
        PluginManager.INSTANCE.execute(pluginName, funcName, request, new RequestServerInterface(session, pluginName));
    }
}
