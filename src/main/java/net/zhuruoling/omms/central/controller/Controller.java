package net.zhuruoling.omms.central.controller;

import net.zhuruoling.omms.central.controller.console.ControllerConsole;
import net.zhuruoling.omms.central.controller.console.output.PrintTarget;
import net.zhuruoling.omms.central.controller.console.input.InputSource;
import net.zhuruoling.omms.central.controller.crashreport.CrashReportStorage;

import java.util.List;

abstract public class Controller {
    public abstract boolean isStatusQueryable();
    public abstract CommandExecutionResult sendCommand(String command) throws Exception;
    public abstract ControllerConsole startControllerConsole(InputSource inputSource, PrintTarget<?,ControllerConsole> printTarget, String id);
    public abstract Status queryControllerStatus();
    public abstract CrashReportStorage convertCrashReport(String raw);

    public abstract String getName();
    public String getDisplayName(){
        return getName();
    }
    public abstract String getType();
}
