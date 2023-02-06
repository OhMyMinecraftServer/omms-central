import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.zhuruoling.omms.central.console.CommandSourceStack
import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.network.http.client.ControllerHttpClient
import net.zhuruoling.omms.central.plugin.LifecycleServerInterface
import net.zhuruoling.omms.central.plugin.PluginDependency
import net.zhuruoling.omms.central.plugin.PluginLogger
import net.zhuruoling.omms.central.plugin.PluginMain
import net.zhuruoling.omms.central.plugin.PluginMetadata

import java.lang.module.ModuleDescriptor

class MyPlugin extends PluginMain {
    PluginLogger logger = null
    PluginMain util = null

    @Override
    void onLoad(LifecycleServerInterface serverInterface) {
        logger = serverInterface.getLogger()
        logger.info("Plugin loaded!")
        serverInterface.registerCommand(LiteralArgumentBuilder.<CommandSourceStack> literal("another_test")
                .executes({

                    return 0
                })
        )
        serverInterface.registerCommand(LiteralArgumentBuilder.<CommandSourceStack> literal("summon_player")
                .executes({
                    var controller = ControllerManager.INSTANCE.controllers.get("out_survival")
                    var client = new ControllerHttpClient(controller.controller())
                    for (i in 0..<1248) {
                        var result = client.sendCommand("player Minecraft$i spawn")
                        for (line in result) {
                            println(i + " " + line)
                        }
                    }
                    return 0
                })
        )
        serverInterface.registerCommand(LiteralArgumentBuilder.<CommandSourceStack> literal("kill_player")
                .executes({
                    var controller = ControllerManager.INSTANCE.controllers.get("out_survival")
                    var client = new ControllerHttpClient(controller.controller())
                    for (i in 0..<1248) {
                        var result = client.sendCommand("player Minecraft$i kill")
                        for (line in result) {
                            println(i + line)
                        }
                    }
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