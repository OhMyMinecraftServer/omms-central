import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.zhuruoling.console.CommandSourceStack
import net.zhuruoling.request.Request
import net.zhuruoling.plugin.LifecycleServerInterface
import net.zhuruoling.plugin.PluginLogger
import net.zhuruoling.plugin.RequestServerInterface
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString

class PluginMain {
    public static String metadata = "{\"id\":\"test_plugin\",\"version\":\"0.0.1\",\"author\":\"ZhuRuoLing\"}"

    void onLoad(LifecycleServerInterface serverInterface) {
        serverInterface.registerRequestCode("TEST", "test")
        serverInterface.registerRequestCode("HELLO","hello")
        PluginLogger logger = serverInterface.getLogger()
        logger.info("Registering Command test!")
        logger.info("Test Plugin loaded!")
        serverInterface.registerCommand("fuck", {
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
    }

    def test(RequestServerInterface serverInterface, Request command) {
        serverInterface.logger.info(command.toString())
        serverInterface.sendBack("OK", new String[]{"wdnmd"})
    }

    def hello(RequestServerInterface serverInterface, Request command){
        serverInterface.logger.info("HELLO COMMAND TRIGGERED")
        serverInterface.sendBack("OK",new String[]{"o/"})
    }

    def onUnload(LifecycleServerInterface serverInterface) {
        serverInterface.getLogger().info("Test Plugin unloaded!")
    }

}

