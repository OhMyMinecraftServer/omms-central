package net.zhuruoling.foo;


import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class Bar {
    public static void main(String[] args) {

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        long total = 0;
        for (int j = 0; j < 100 ; j++){
            Thread thread1 = new Thread(() -> {
                for (int i = 0; i < 10000; i++) {
                    System.out.println(i);
                }
            });
            Thread thread2 = new Thread(() -> {
                for (int i = 0; i < 10000; i++) {
                    System.out.println(i);
                }
            });
            Thread thread3 = new Thread(() -> {
                for (int i = 0; i < 10000; i++) {
                    System.out.println(i);
                }
            });

            Thread thread4 = new Thread(() -> {
                for (int i = 0; i < 10000; i++) {
                    System.out.println(i);
                }
            });
            long beginTimeMillis = System.currentTimeMillis();
            System.out.printf("begin at :%d%n", beginTimeMillis);
            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();
            while (thread1.isAlive() || thread2.isAlive() || thread3.isAlive() || thread4.isAlive()) {

            }
            long endTimeMillis = System.currentTimeMillis();
            System.out.printf("end at: %d%n", endTimeMillis);
            System.out.printf("time elapsed: %d%n", endTimeMillis - beginTimeMillis);
            total += endTimeMillis - beginTimeMillis;
        }
        System.out.printf("avg: %f%n", total / 100.0);

    }


}
