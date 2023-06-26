package net.zhuruoling.omms.central.controller.console.output;

import net.zhuruoling.omms.central.controller.console.ControllerConsole;

import java.io.PrintStream;

public class StdOutPrintTarget extends PrintTarget<PrintStream, ControllerConsole> {
    public StdOutPrintTarget() {
        super(System.out);
    }

    @Override
    void println(PrintStream target, ControllerConsole context, String content) {
        target.print("[" + context.getConsoleId() + "] " + content + ((content.endsWith("\n") || content.endsWith("\r\n") || content.endsWith("\r")) ? "" : "\n"));
    }
}
