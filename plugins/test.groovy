import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.zhuruoling.omms.central.announcement.AnnouncementManager
import net.zhuruoling.omms.central.console.CommandSourceStack
import net.zhuruoling.omms.central.controller.ControllerManager
import net.zhuruoling.omms.central.network.broadcast.Broadcast
import net.zhuruoling.omms.central.network.session.request.Request
import net.zhuruoling.omms.central.network.session.response.Response
import net.zhuruoling.omms.central.plugin.LifecycleServerInterface
import net.zhuruoling.omms.central.plugin.PluginDependency
import net.zhuruoling.omms.central.plugin.PluginLogger
import net.zhuruoling.omms.central.plugin.PluginMain
import net.zhuruoling.omms.central.plugin.PluginMetadata
import net.zhuruoling.omms.central.plugin.RequestServerInterface
import net.zhuruoling.omms.central.system.SystemInfo
import net.zhuruoling.omms.central.system.SystemUtil
import net.zhuruoling.omms.central.network.session.response.Result
import net.zhuruoling.omms.central.util.Util
import net.zhuruoling.omms.central.whitelist.WhitelistManager
import net.zhuruoling.omms.central.network.session.request.InitRequest;

import java.lang.module.ModuleDescriptor

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString

class TestPlugin extends PluginMain {
    @Override
    void onLoad(LifecycleServerInterface serverInterface) {
        serverInterface.registerRequestCode("TEST", "test")
        serverInterface.registerRequestCode("PING", {
            RequestServerInterface requestServerInterface, Request command ->
            requestServerInterface.logger.info("PING COMMAND TRIGGERED")
            return new Response().withResponseCode(Result.OK).withContentPair("message", "pong")
        })

        PluginLogger logger = serverInterface.getLogger()
        serverInterface.registerCommand("shit" , {
            logger.info(Util.toJson(ControllerManager.INSTANCE.controllers.get("creative")))
            logger.info(Util.toJson(AnnouncementManager.INSTANCE.announcementMap.get("LxNHS17krC1ajqBL")))
            logger.info(Util.toJson(WhitelistManager.INSTANCE.whitelists[0]))
            logger.info(Util.toJson(SystemUtil.getSystemInfo()))
            logger.info(Util.toJson(new InitRequest(Util.PROTOCOL_VERSION).withContentKeyPair("token", "connCode")))
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

    Response test(RequestServerInterface serverInterface, Request command) {
        serverInterface.logger.info(command.toString())
        //serverInterface.sendBack("OK", new String[]{"wdnmd"})
        return new Response().withResponseCode(Result.OK).withContentPair("message","wdnmd")
    }

    @Override
    void onUnload(LifecycleServerInterface serverInterface) {
        serverInterface.getLogger().info("Test Plugin unloaded!")
    }

    @Override
    PluginMetadata getPluginMetadata() {
        ArrayList<PluginDependency.Dependency> dependencies = new ArrayList<>()
        dependencies.add(new PluginDependency.Dependency() {
            @Override
            String getId() {
                return "omms-central"
            }

            @Override
            PluginDependency.Operator getOperator() {
                return PluginDependency.Operator.GREATER
            }

            @Override
            ModuleDescriptor.Version getVersion() {
                return ModuleDescriptor.Version.parse("0.5.4")
            }
        })
        dependencies.add(PluginDependency.Dependency.of("another-dependency", PluginDependency.Operator.GREATER, "0.0.1"))

        PluginDependency dependency = new PluginDependency(dependencies)
        return new PluginMetadata("test", "0.0.1", "ZhuRuoLing", dependency)
    }
}

