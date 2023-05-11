package net.zhuruoling.omms.central.controller;

import net.zhuruoling.omms.central.controller.console.ControllerConsole;
import net.zhuruoling.omms.central.controller.console.input.PrintTarget;
import net.zhuruoling.omms.central.controller.console.output.InputSource;

import java.util.List;

abstract public class Controller {
    public abstract boolean isStatusQueryable();
    public abstract List<String> sendCommand(String command);
    public abstract ControllerConsole startControllerConsole(InputSource inputSource, PrintTarget<?,?> printTarget, String id);
    public abstract Status getControllerStatus();

    public abstract String getName();

    public abstract String getType();
}
