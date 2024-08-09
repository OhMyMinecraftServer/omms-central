package icu.takeneko.omms.central.controller.console.output;

import icu.takeneko.omms.central.controller.console.ControllerConsole;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.List;

public class StdOutPrintTarget extends PrintTarget<PrintStream, ControllerConsole> {
    public StdOutPrintTarget() {
        super(System.out);
    }

    @Override
    public void printMultiLine(List<String> content, ControllerConsole ctx) {
        content.forEach(it -> println(it, ctx));
    }

    @Override
    void println(@NotNull PrintStream target, @NotNull ControllerConsole context, @NotNull String content) {
        target.print("[" + context.getConsoleId() + "] " + content + ((content.endsWith("\n") || content.endsWith("\r\n") || content.endsWith("\r")) ? "" : "\n"));
    }
}
