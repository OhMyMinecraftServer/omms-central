package net.zhuruoling.omms.central.util.io;

public abstract class PrintTarget<T> {
    T target;

    public PrintTarget(T target) {
        this.target = target;
    }
    public void println(String content){
        println(target,content);
    }

    abstract void println(T target, String content);
}
