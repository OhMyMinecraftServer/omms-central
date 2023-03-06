package net.zhuruoling.omms.central.util.io;

import java.io.PrintStream;

public class StdOutPrintTarget extends PrintTarget<PrintStream> {
    public StdOutPrintTarget() {
        super(System.out);
    }

    @Override
    void println(PrintStream target, String content) {
        target.println(content);
    }
}
