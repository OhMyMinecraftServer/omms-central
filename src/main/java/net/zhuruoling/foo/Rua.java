package net.zhuruoling.foo;

import net.zhuruoling.util.Util;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class Rua {
    public static void main(String[] args) {
        //SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        for (String builtinCommand : Util.BUILTIN_COMMANDS) {
            System.out.printf("case \"%s\" -> {}%n", builtinCommand);
        }
    }
}
