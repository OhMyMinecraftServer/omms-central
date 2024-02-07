package icu.takeneko.omms.central.controller.console.output;

public abstract class PrintTarget<T, K> {
    T target;

    public PrintTarget(T target) {
        this.target = target;
    }

    public void println(String content, K context){
        synchronized (this){
            println(target, context, content);
        }
    }

    abstract void println(T target, K context, String content);
}
