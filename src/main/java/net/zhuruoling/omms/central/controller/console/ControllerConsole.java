package net.zhuruoling.omms.central.controller.console;

import net.zhuruoling.omms.central.controller.Controller;
import net.zhuruoling.omms.central.controller.console.input.InputSource;

public interface ControllerConsole {
    Controller getController();

//    static ControllerConsole newInstance(Controller controller, String consoleId, PrintTarget<?, ControllerConsole> printTarget, InputSource inputSource){
//        return null;
//    }

    String getConsoleId();

    void start();

    boolean isAlive();

    InputSource getInputSource();

    void close();
}
