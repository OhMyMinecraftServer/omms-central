package net.zhuruoling.omms.central.plugin;

import net.zhuruoling.omms.central.main.RuntimeConstants;
import net.zhuruoling.omms.central.network.session.HandlerSession;

public abstract class ServerInterface {
    private final HandlerSession session;
    private final PluginLogger logger;
    private final String pluginName;
    public ServerInterface(HandlerSession handlerSession, String name) {
        this.session = handlerSession;
        this.pluginName = name;
        this.logger = new PluginLogger(this.pluginName);

    }
    public Object invokePluginApiMethod(String apiProviderId, String methodName, Object... args){
        var map = RuntimeConstants.INSTANCE.getPluginDeclaredApiMethod().get(apiProviderId);
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

    public PluginLogger getLogger() {
        return logger;
    }

    public HandlerSession getSession() {
        return session;
    }

    public String getPluginName() {
        return pluginName;
    }
}
