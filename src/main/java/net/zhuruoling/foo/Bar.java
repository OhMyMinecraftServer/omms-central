package net.zhuruoling.foo;


import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.util.Scanner;

public class Bar {
    public static void main(String[] args) {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        int total = 0;
        Scanner scanner = new Scanner(System.in);
        total = scanner.nextInt();
        long sum = 0;
        for (int i = 0; i < total; i++) {
            int k = 0;
            long beginNanoTime = System.nanoTime();
            for (int j = 0; j < 1e5 + 1; j++) {
                k++;
            }
            long endNanoTime = System.nanoTime();
            sum += endNanoTime - beginNanoTime;
            //System.out.println(endNanoTime - beginNanoTime);
        }
        System.out.println(sum / total);
    }


}
