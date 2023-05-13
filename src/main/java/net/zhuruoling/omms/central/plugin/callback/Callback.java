package net.zhuruoling.omms.central.plugin.callback;

import net.zhuruoling.omms.central.whitelist.WhitelistManager;

import java.util.List;
import java.util.function.Consumer;

public abstract class Callback<T> {
    protected List<Consumer<T>> consumers;

    abstract void register(Consumer<T> consumer);

    public void invokeAll(T t){
        for (Consumer<T> consumer : consumers) {
            consumer.accept(t);
        }
    }
}
