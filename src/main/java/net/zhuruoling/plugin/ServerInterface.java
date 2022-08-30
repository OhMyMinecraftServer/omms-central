package net.zhuruoling.plugin;

import net.zhuruoling.session.HandlerSession;
public abstract class ServerInterface {
    private final HandlerSession session;
    private final PluginLogger logger;
    private final String pluginName;
    public ServerInterface(HandlerSession handlerSession, String name) {
        this.session = handlerSession;
        this.pluginName = name;
        this.logger = new PluginLogger(this.pluginName);
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
