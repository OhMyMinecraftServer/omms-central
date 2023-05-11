package net.zhuruoling.omms.central.main;

import net.zhuruoling.omms.central.controller.Controller;
import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.controller.console.ControllerConsole;
import net.zhuruoling.omms.central.controller.console.input.StdOutPrintTarget;
import net.zhuruoling.omms.central.controller.console.output.StdinInputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RemoteControllerConsoleMain {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("Main");
        int index = -1;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--id")) {
                index = i;
                break;
            }
        }
        index++;
        String controllerId;
        try {
            controllerId = args[index];
        } catch (Exception e) {
            logger.error("Cannot resolve args.", e);
            return;
        }
        ControllerManager.INSTANCE.init();
        Controller controllerImpl = Objects.requireNonNull(ControllerManager.INSTANCE.getControllerByName(controllerId)).controller();
//        StdOutPrintTarget stdOutPrintTarget = ;
//        ControllerConsole controllerConsole = new ControllerConsole(controllerImpl, controllerId, stdOutPrintTarget, new StdinInputSource());
        String id = "MAIN";
        ControllerConsole controllerConsole = controllerImpl.startControllerConsole(new StdinInputSource(),new StdOutPrintTarget(), id);
        controllerConsole.start();
        while (controllerConsole.isAlive()) {
            Thread.yield();
        }
    }
}
