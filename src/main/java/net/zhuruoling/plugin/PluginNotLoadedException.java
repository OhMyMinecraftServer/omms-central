package net.zhuruoling.plugin;

public class PluginNotLoadedException extends IllegalStateException{
    public PluginNotLoadedException(String id) {
        super("Plugin %s not loaded or is in a wrong PluginState.".formatted(id));
    }
}
