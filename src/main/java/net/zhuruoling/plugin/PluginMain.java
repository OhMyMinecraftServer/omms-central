package net.zhuruoling.plugin;

public abstract class PluginMain {
    public abstract void onLoad(LifecycleServerInterface lifecycleServerInterface);
    public abstract void onUnload(LifecycleServerInterface lifecycleServerInterface);
    public abstract PluginMetadata getPluginMetadata();
}
