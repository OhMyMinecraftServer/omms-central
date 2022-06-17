package net.zhuruoling.util;

public class PluginAlreadyLoadedException extends RuntimeException{
    public PluginAlreadyLoadedException(String message) {
        super(message);
    }
}
