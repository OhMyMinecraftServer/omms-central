package net.zhuruoling.omms.central.script;

public class ScriptNotLoadedException extends IllegalStateException{
    public ScriptNotLoadedException(String id) {
        super("Plugin %s not loaded or is in a wrong PluginState.".formatted(id));
    }
}
