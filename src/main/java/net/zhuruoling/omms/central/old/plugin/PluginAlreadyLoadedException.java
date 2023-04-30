package net.zhuruoling.omms.central.old.plugin;

public class PluginAlreadyLoadedException extends RuntimeException{
    public PluginAlreadyLoadedException(String message) {
        super(message);
    }
}
