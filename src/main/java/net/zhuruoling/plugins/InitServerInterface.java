package net.zhuruoling.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitServerInterface {
    PluginLogger logger = null;
    public InitServerInterface(String name){
        logger = new PluginLogger(name);
    }

    public void registerRequestCode(int code, String functionName){

    }
}
