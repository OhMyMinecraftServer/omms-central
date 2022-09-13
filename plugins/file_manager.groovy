import net.zhuruoling.network.session.request.Request
import net.zhuruoling.plugin.LifecycleServerInterface
import net.zhuruoling.plugin.PluginLogger
import net.zhuruoling.plugin.PluginMain
import net.zhuruoling.plugin.PluginMetadata
import net.zhuruoling.plugin.RequestServerInterface

import java.nio.file.Files
import java.nio.file.Path

class FileManager extends PluginMain {
    //public static String metadata = "{\"id\":\"file_manager\",\"version\":\"0.0.1\",\"author\":\"ZhuRuoLing\"}"
    @Override
    void onLoad(LifecycleServerInterface serverInterface) {
        serverInterface.registerRequestCode("READ_FILE", "readFile")
        PluginLogger logger = serverInterface.getLogger()
        logger.info("Hello World!")
        logger.error("wdnmd")

    }


    def readFile(RequestServerInterface serverInterface, Request request) {
        serverInterface.logger.info(request.toString())
        String fileName = request.load[1]
        if (Files.exists(Path.of(fileName))){
            String[] lines = Files.readAllLines(Path.of(fileName))
            serverInterface.sendBack("OK", lines)
        }
        else {
            serverInterface.sendBack("FILE_NOT_EXIST", new String[]{"FUCK U"})
        }

    }

    @Override
    void onUnload(LifecycleServerInterface serverInterface) {
        serverInterface.getLogger().info("Bye")
    }

    @Override
    PluginMetadata getPluginMetadata() {
        return new PluginMetadata("file_manager", "0.0.1", "ZhuRuoLing")
    }
}
