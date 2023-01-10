package net.zhuruoling.omms.central.plugin;

public class PluginAlreadyLoadedException extends RuntimeException{
    public PluginAlreadyLoadedException(String message) {
        super(message);
    }
}
