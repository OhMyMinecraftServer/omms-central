package net.zhuruoling.main;

import net.zhuruoling.console.CommandSourceStack;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        System.out.println("Starting net.zhuruoling.main.Main");
        MainKt.main(args);

    }
}
