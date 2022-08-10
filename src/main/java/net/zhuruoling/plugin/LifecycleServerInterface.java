package net.zhuruoling.plugin;

import net.zhuruoling.command.CommandManager;
import net.zhuruoling.handler.PluginCommandHandler;

public class LifecycleServerInterface {
    PluginLogger logger;
    String pluginName;
    public LifecycleServerInterface(String name){
        logger = new PluginLogger(name);
        this.pluginName = name;
    }

    public void registerRequestCode(String code, String functionName){
        logger.info("Registering %s -> %s".formatted(code,functionName));
        CommandManager.INSTANCE.registerCommand(code,new PluginCommandHandler(pluginName,code,functionName));
    }

    public PluginLogger getLogger() {
        return logger;
    }
}
