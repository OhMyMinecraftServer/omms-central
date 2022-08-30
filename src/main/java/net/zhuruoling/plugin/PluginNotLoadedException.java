package net.zhuruoling.plugin;

public class PluginNotLoadedException extends RuntimeException{
    public PluginNotLoadedException(String message) {
        super(message);
    }
}
