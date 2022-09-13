import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.zhuruoling.console.CommandSourceStack
import net.zhuruoling.network.session.request.Request
import net.zhuruoling.plugin.LifecycleServerInterface
import net.zhuruoling.plugin.PluginLogger
import net.zhuruoling.plugin.PluginMain
import net.zhuruoling.plugin.PluginMetadata
import net.zhuruoling.plugin.RequestServerInterface
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString

class TestPlugin extends PluginMain {
   // public static String metadata = "{\"id\":\"test_plugin\",\"version\":\"0.0.1\",\"author\":\"ZhuRuoLing\"}"

    @Override
    void onLoad(LifecycleServerInterface serverInterface) {
        serverInterface.registerRequestCode("TEST", "test")
        serverInterface.registerRequestCode("PING", {RequestServerInterface requestServerInterface, Request command ->
            requestServerInterface.logger.info("PING COMMAND TRIGGERED")
            requestServerInterface.sendBack("OK",new String[]{"PONG"})
        })
        PluginLogger logger = serverInterface.getLogger()
        serverInterface.registerCommand("shit" , {
            logger.info("Executed command test with params $it")
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

    def test(RequestServerInterface serverInterface, Request command) {
        serverInterface.logger.info(command.toString())
        serverInterface.sendBack("OK", new String[]{"wdnmd"})
    }

    @Override
    void onUnload(LifecycleServerInterface serverInterface) {
        serverInterface.getLogger().info("Test Plugin unloaded!")
    }

    @Override
    PluginMetadata getPluginMetadata() {
        return new PluginMetadata("test", "0.0.1", "ZhuRuoLing")
    }
}

