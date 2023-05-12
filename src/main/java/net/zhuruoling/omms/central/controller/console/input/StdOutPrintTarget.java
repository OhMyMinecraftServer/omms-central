package net.zhuruoling.omms.central.controller.console.input;

import net.zhuruoling.omms.central.controller.console.ControllerConsole;
import net.zhuruoling.omms.central.controller.console.ControllerConsoleImpl;

import java.io.PrintStream;

public class StdOutPrintTarget extends PrintTarget<PrintStream, ControllerConsole> {
    public StdOutPrintTarget() {
        super(System.out);
    }

    @Override
    void println(PrintStream target, ControllerConsole context, String content) {
        target.println("[" + context.getConsoleId() +"] " + content);
    }
}
