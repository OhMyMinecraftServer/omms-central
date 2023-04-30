package net.zhuruoling.omms.central.old.plugin;

import net.zhuruoling.omms.central.GlobalVariable;
import net.zhuruoling.omms.central.network.session.SessionContext;
import org.jetbrains.annotations.NotNull;

public abstract class OperationProxy {
    private final SessionContext session;
    private final @NotNull PluginLogger logger;
    private final String pluginName;
    public OperationProxy(SessionContext sessionContext, String name) {
        this.session = sessionContext;
        this.pluginName = name;
        this.logger = new PluginLogger(this.pluginName);

    }
    public Object invokePluginApiMethod(@NotNull String apiProviderId, String methodName, Object... args){
        var map = GlobalVariable.INSTANCE.getPluginDeclaredApiMethod().get(apiProviderId);
        if (map == null){
            throw new PluginNotExistException("Plugin %s not exist".formatted(apiProviderId));
        }
        var method = map.get(methodName);
        if (method == null){
            throw new RuntimeException(new NoSuchMethodException("Method %s not exist".formatted(methodName)));
        }
        try {
            method.setAccessible(true);
            return method.invoke(PluginManager.INSTANCE.getPluginInstance(apiProviderId).getInstance(), args);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public @NotNull PluginLogger getLogger() {
        return logger;
    }

    public SessionContext getSession() {
        return session;
    }

    public String getPluginName() {
        return pluginName;
    }
}