package net.zhuruoling.omms.central.plugin.handler;

import net.zhuruoling.omms.central.network.session.handler.RequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class PluginRequestHandler extends RequestHandler {
    public PluginRequestHandler(String pluginId) {
        super("PLUGIN_"+pluginId);
    }

    @Override
    public @Nullable Permission requiresPermission() {
        return Permission.EXECUTE_PLUGIN_REQUEST;
    }

    public abstract @NotNull String getRequestCode();
}
