package net.zhuruoling.omms.central.plugin;

public class PluginNotExistException extends RuntimeException{
    public PluginNotExistException(String message) {
        super(message);
    }
}
