package net.zhuruoling.foo;

public class LockedWhitelist {
     String query(String player){
        synchronized (this)
        {
            System.out.printf("1Querying player %s in thread %s%n", player, Thread.currentThread().getName());
            System.out.printf("11Querying player %s in thread %s%n", player, Thread.currentThread().getName());
            System.out.printf("111Querying player %s in thread %s%n", player, Thread.currentThread().getName());
            System.out.printf("1111Querying player %s in thread %s%n", player, Thread.currentThread().getName());
            System.out.printf("11111Querying player %s in thread %s%n", player, Thread.currentThread().getName());
            System.out.printf("111111Querying player %s in thread %s%n", player, Thread.currentThread().getName());
            return "";
        }
    }
}
