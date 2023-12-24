package net.zhuruoling.omms.central.plugin;

abstract public class PluginMain {
    public abstract void onInitialize();

    public void preOnReload(){}

    public void postOnReload(){}
}
