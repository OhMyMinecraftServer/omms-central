package net.zhuruoling.foo;


import net.zhuruoling.gui.GuiMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class Rua {
    private static final Logger logger = LoggerFactory.getLogger("Rua");
    public static void main(String[] args) throws InterruptedException {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        GuiMain guiMain = new GuiMain();
        guiMain.show();
        while (true){
            Thread.sleep(10);
        }
    }
}
