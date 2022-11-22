import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.zhuruoling.console.CommandSourceStack
import net.zhuruoling.plugin.LifecycleServerInterface
import net.zhuruoling.plugin.PluginLogger
import net.zhuruoling.plugin.PluginMain
import net.zhuruoling.plugin.PluginMetadata

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

    class Util{

    }

}