package icu.takeneko.omms.central.controller.console.output;

import java.util.List;

public abstract class PrintTarget<T, K> {
    T target;

    public PrintTarget(T target) {
        this.target = target;
    }

    public abstract void printMultiLine(List<String> content, K ctx);

    public void println(String content, K context) {
        synchronized (this) {
            println(target, context, content);
        }
    }

    abstract void println(T target, K context, String content);
}
