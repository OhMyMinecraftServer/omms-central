package net.zhuruoling.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginLogger {
    Logger logger = LoggerFactory.getLogger("InitServerInterface");
    String pluginName;
    public PluginLogger(String name){
        this.pluginName = name;
    }

    public void info(String content){
        this.logger.info("[%s]:%s".formatted(pluginName, content));
    }
    public void debug(String content){
        this.logger.debug("[%s]:%s".formatted(pluginName, content));
    }
    public void error(String content){
        this.logger.error("[%s]:%s".formatted(pluginName, content));
    }
    public void warn(String content){
        this.logger.warn("[%s]:%s".formatted(pluginName, content));
    }

}
