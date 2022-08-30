package net.zhuruoling.plugin;

import net.zhuruoling.request.RequestManager;
import net.zhuruoling.handler.PluginRequestHandler;

public class LifecycleServerInterface extends ServerInterface {


    public LifecycleServerInterface(String name){
        super(null, name);
    }

    public void registerRequestCode(String code, String functionName){
        this.getLogger().info("Registering %s -> %s".formatted(code,functionName));
        RequestManager.INSTANCE.registerRequest(code,new PluginRequestHandler(this.getPluginName(),code,functionName));
    }

}
