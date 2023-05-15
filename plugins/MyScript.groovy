import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.zhuruoling.omms.central.command.CommandSourceStack
import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.network.http.client.ControllerHttpClient
import net.zhuruoling.omms.central.script.LifecycleOperationInterface

import net.zhuruoling.omms.central.script.ScriptLogger
import net.zhuruoling.omms.central.script.ScriptMain
import net.zhuruoling.omms.central.script.ScriptMetadata
import net.zhuruoling.omms.central.script.ScriptMain

import java.lang.module.ModuleDescriptor

class MyScript extends ScriptMain {
    ScriptLogger logger = null
    ScriptMain util = null

    @Override
    void onLoad(LifecycleOperationInterface serverInterface) {
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
                    var client = new ControllerHttpClient(controller.controllerImpl())
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
                    var client = new ControllerHttpClient(controller.controllerImpl())
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
    void onUnload(LifecycleOperationInterface serverInterface) {
        logger = serverInterface.getLogger()
        logger.info("Plugin loaded!")
    }

    @Override
    ScriptMetadata getPluginMetadata() {
        return new ScriptMetadata("my_plugin", ModuleDescriptor.Version.parse("0.5.4"), "ZhuRuoLing")
    }
}