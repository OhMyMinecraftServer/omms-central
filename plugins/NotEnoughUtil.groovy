import net.zhuruoling.omms.central.plugin.Api
import net.zhuruoling.omms.central.plugin.LifecycleServerInterface
import net.zhuruoling.omms.central.plugin.PluginLogger
import net.zhuruoling.omms.central.plugin.PluginMain
import net.zhuruoling.omms.central.plugin.PluginMetadata

import java.lang.module.ModuleDescriptor

class NotEnougnUtil extends PluginMain {
    PluginLogger logger = null

    @Override
    void onLoad(LifecycleServerInterface serverInterface) {
        logger = serverInterface.getLogger()
        logger.info("KONNICHIWA ZAWARUDO!")
    }

    @Override
    void onUnload(LifecycleServerInterface serverInterface) {
        logger = serverInterface.getLogger()
    }

    @Override
    PluginMetadata getPluginMetadata() {
        return new PluginMetadata("not_enough_util", ModuleDescriptor.Version.parse("0.0.1"), "ZhuRuoLing")
    }

    @Api
    String reverseString(String s){
        return s.reverse()
    }

}