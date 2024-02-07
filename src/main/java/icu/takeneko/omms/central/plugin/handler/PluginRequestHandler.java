package icu.takeneko.omms.central.plugin.handler;

import icu.takeneko.omms.central.network.session.handler.RequestHandler;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.network.session.handler.RequestHandler;
import icu.takeneko.omms.central.permission.Permission;
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
