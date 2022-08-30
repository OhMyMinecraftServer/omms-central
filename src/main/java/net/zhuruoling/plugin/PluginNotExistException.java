package net.zhuruoling.plugin;

public class PluginNotExistException extends RuntimeException{
    public PluginNotExistException(String message) {
        super(message);
    }
}
