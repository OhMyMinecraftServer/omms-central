package net.zhuruoling.omms.central.old.plugin;

public abstract class PluginMain {
    public abstract void onLoad(LifecycleOperationProxy lifecycleServerInterface);
    public abstract void onUnload(LifecycleOperationProxy lifecycleServerInterface);
    public abstract PluginMetadata getPluginMetadata();
}
