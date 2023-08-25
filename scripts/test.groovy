import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.zhuruoling.omms.central.announcement.AnnouncementManager
import net.zhuruoling.omms.central.command.CommandSourceStack
import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.network.chatbridge.Broadcast
import net.zhuruoling.omms.central.network.session.request.Request
import net.zhuruoling.omms.central.network.session.response.Response
import net.zhuruoling.omms.central.script.LifecycleOperationInterface

import net.zhuruoling.omms.central.script.ScriptLogger
import net.zhuruoling.omms.central.script.ScriptMain
import net.zhuruoling.omms.central.script.ScriptMetadata

import net.zhuruoling.omms.central.script.ScriptMain
import net.zhuruoling.omms.central.system.SystemInfo
import net.zhuruoling.omms.central.system.SystemUtil
import net.zhuruoling.omms.central.network.session.response.Result
import net.zhuruoling.omms.central.util.Util
import net.zhuruoling.omms.central.whitelist.WhitelistManager
import net.zhuruoling.omms.central.network.session.request.LoginRequest

import java.lang.module.ModuleDescriptor

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString

class TestScript extends ScriptMain {
    @Override
    void onLoad(LifecycleOperationInterface serverInterface) {
        ScriptLogger logger = serverInterface.getLogger()
        serverInterface.registerCommand("shit" , {
            logger.info(Util.toJson(ControllerManager.INSTANCE.controllers.get("creative")))
            logger.info(Util.toJson(AnnouncementManager.INSTANCE.announcementMap.get("LxNHS17krC1ajqBL")))
            logger.info(Util.toJson(WhitelistManager.INSTANCE.whitelists[0]))
            logger.info(Util.toJson(SystemUtil.getSystemInfo()))
            logger.info(Util.toJson(new LoginRequest(Util.PROTOCOL_VERSION).withContentKeyPair("token", "connCode")))
        })
        serverInterface.registerCommand(LiteralArgumentBuilder.<CommandSourceStack>literal("another_test")
                .then(RequiredArgumentBuilder<CommandSourceStack, String>.argument("some_string", greedyString()).executes({
                    String arg = StringArgumentType.getString(it, "some_string")
                    logger.info("Called another test with argument $arg.")
                    return 0
                }))
                .executes({
                    logger.info("Called another test with no argument.")
                    return 0
                })
        )
        logger.info("Test Plugin loaded!")

    }
    @Override
    void onUnload(LifecycleOperationInterface serverInterface) {
        serverInterface.getLogger().info("Test Plugin unloaded!")
    }

    @Override
    ScriptMetadata getPluginMetadata() {
        return new ScriptMetadata("test", "0.0.1", "ZhuRuoLing")
    }
}

