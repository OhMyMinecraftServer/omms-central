package net.zhuruoling.foo;


import net.zhuruoling.graphics.GraphicsUtilKt;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.util.Scanner;

public class Bar {
    public static void main(String[] args) {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        Scanner scanner = new Scanner(System.in);
        GraphicsUtilKt.info();
    }


}
