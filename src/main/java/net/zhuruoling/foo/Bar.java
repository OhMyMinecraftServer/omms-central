package net.zhuruoling.foo;


import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class Bar {
    public static void main(String[] args) {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        LockedWhitelist lockedWhitelist = new LockedWhitelist();
        Thread thread1 = new Thread(() -> {
            while (true){
                lockedWhitelist.query("wdnmd");
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "A");
        Thread thread2 = new Thread(() -> {
            while (true){
                lockedWhitelist.query("wdnmd");
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "B");
        thread2.start();
        thread1.start();
    }


}
