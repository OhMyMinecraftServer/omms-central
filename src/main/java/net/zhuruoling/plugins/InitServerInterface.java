package net.zhuruoling.plugins;

public class InitServerInterface {
    PluginLogger logger = null;
    public InitServerInterface(String name){
        logger = new PluginLogger(name);
    }

    public void registerRequestCode(int code, String functionName){
        logger.info("Registering %s -> %s".formatted(code,functionName));

    }

    public PluginLogger getLogger() {
        return logger;
    }
}
