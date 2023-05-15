package net.zhuruoling.omms.central.script;

public abstract class ScriptMain {
    public abstract void onLoad(LifecycleOperationInterface lifecycleServerInterface);
    public abstract void onUnload(LifecycleOperationInterface lifecycleServerInterface);
    public abstract ScriptMetadata getPluginMetadata();
}
