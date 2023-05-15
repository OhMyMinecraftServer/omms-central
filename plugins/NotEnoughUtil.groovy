import net.zhuruoling.omms.central.script.Api
import net.zhuruoling.omms.central.script.LifecycleOperationInterface
import net.zhuruoling.omms.central.script.ScriptLogger
import net.zhuruoling.omms.central.script.ScriptMain
import net.zhuruoling.omms.central.script.ScriptMetadata

import java.lang.module.ModuleDescriptor

class NotEnougnUtil extends ScriptMain {
    ScriptLogger logger = null

    @Override
    void onLoad(LifecycleOperationInterface serverInterface) {
        logger = serverInterface.getLogger()
        logger.info("KONNICHIWA ZAWARUDO!")
    }

    @Override
    void onUnload(LifecycleOperationInterface serverInterface) {
        logger = serverInterface.getLogger()
    }

    @Override
    ScriptMetadata getPluginMetadata() {
        return new ScriptMetadata("not_enough_util", ModuleDescriptor.Version.parse("0.0.1"), "ZhuRuoLing")
    }

    @Api
    String reverseString(String s){
        return s.reverse()
    }

}