package net.zhuruoling.plugin;

public class PluginAlreadyLoadedException extends RuntimeException{
    public PluginAlreadyLoadedException(String message) {
        super(message);
    }
}
