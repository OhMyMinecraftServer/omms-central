package net.zhuruoling.omms.central.main;

import net.zhuruoling.omms.central.controller.Controller;
import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.controller.console.ControllerConsoleImpl;
import net.zhuruoling.omms.central.controller.console.output.StdOutPrintTarget;
import net.zhuruoling.omms.central.controller.console.input.StdinInputSource;
import net.zhuruoling.omms.central.util.UtilKt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RemoteControllerConsoleMain {
    public static void main(String @NotNull [] args) {
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
        Controller controllerImpl = Objects.requireNonNull(ControllerManager.INSTANCE.getControllerByName(controllerId));
//        StdOutPrintTarget stdOutPrintTarget = ;
//        ControllerConsole controllerConsole = new ControllerConsole(controllerImpl, controllerId, stdOutPrintTarget, new StdinInputSource());
        String id = "MAIN";
        ControllerConsoleImpl controllerConsoleImpl = (ControllerConsoleImpl) controllerImpl.startControllerConsole(new StdinInputSource().withHistory(UtilKt.getOrCreateControllerHistory(controllerId)),new StdOutPrintTarget(), id);
        controllerConsoleImpl.start();
        while (controllerConsoleImpl.isAlive()) {
            Thread.yield();
        }
    }
}
