package net.zhuruoling.plugin;

import net.zhuruoling.command.CommandManager;
import net.zhuruoling.handler.PluginCommandHandler;

public class InitServerInterface {
    PluginLogger logger;
    String pluginName;
    public InitServerInterface(String name){
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
