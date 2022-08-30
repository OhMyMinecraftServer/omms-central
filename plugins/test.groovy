import net.zhuruoling.request.Request
import net.zhuruoling.plugin.LifecycleServerInterface
import net.zhuruoling.plugin.PluginLogger
import net.zhuruoling.plugin.RequestServerInterface

class PluginMain {
    public static String metadata = "{\"id\":\"test_plugin\",\"version\":\"0.0.1\",\"author\":\"ZhuRuoLing\"}"

    void onLoad(LifecycleServerInterface serverInterface) {
        serverInterface.registerRequestCode("TEST", "test")
        serverInterface.registerRequestCode("HELLO","hello")
        PluginLogger logger = serverInterface.getLogger()
        logger.info("Test Plugin loaded!")
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
