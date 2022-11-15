import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.zhuruoling.console.CommandSourceStack
import net.zhuruoling.plugin.LifecycleServerInterface
import net.zhuruoling.plugin.PluginDependency
import net.zhuruoling.plugin.PluginLogger
import net.zhuruoling.plugin.PluginMain
import net.zhuruoling.plugin.PluginMetadata

import java.lang.module.ModuleDescriptor

class MyPlugin extends PluginMain {
    PluginLogger logger = null
    PluginMain util = null
    @Override
    void onLoad(LifecycleServerInterface serverInterface){
        logger = serverInterface.getLogger()
        logger.info("Plugin loaded!")
        serverInterface.registerCommand(LiteralArgumentBuilder.<CommandSourceStack>literal("another_test")
                .executes({

                    return 0
                })
        )
        util = serverInterface.require("not_enough_util")
    }

    @Override
    void onUnload(LifecycleServerInterface serverInterface) {
        logger = serverInterface.getLogger()
        logger.info("Plugin loaded!")
    }

    @Override
    PluginMetadata getPluginMetadata() {
        return new PluginMetadata("my_plugin", ModuleDescriptor.Version.parse("0.5.4"), "ZhuRuoLing",
                new PluginDependency(List.of(PluginDependency.Dependency.of("not_enough_util", PluginDependency.Operator.GREATER, "0.0.1"))))
    }
}