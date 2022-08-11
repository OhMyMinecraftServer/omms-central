import net.zhuruoling.command.Command
import net.zhuruoling.plugin.LifecycleServerInterface
import net.zhuruoling.plugin.PluginLogger
import net.zhuruoling.plugin.RequestServerInterface

class PluginMain {
    public static String metadata = "{\"id\":\"test_plugin\",\"version\":\"0.0.1\",\"author\":\"ZhuRuoLing\"}"

    void onLoad(LifecycleServerInterface serverInterface) {
        serverInterface.registerRequestCode("TEST", "test")
        PluginLogger logger = serverInterface.getLogger()
        logger.info("Test Plugin loaded!")
    }

    def test(RequestServerInterface serverInterface, Command command) {
        serverInterface.logger.info(command.toString())
        serverInterface.sendBack("OK", new String[]{"wdnmd"})
    }

    def onUnload(LifecycleServerInterface serverInterface) {
        serverInterface.getLogger().info("Test Plugin unloaded!")
    }
}
