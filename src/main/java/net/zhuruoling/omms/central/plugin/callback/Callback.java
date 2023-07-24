package net.zhuruoling.omms.central.plugin.callback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Callback<T> {
    protected @NotNull List<Consumer<T>> consumers = new ArrayList<>();

    abstract public void register(Consumer<T> consumer);

    public void invokeAll(T t){
        for (Consumer<T> consumer : consumers) {
            consumer.accept(t);
        }
    }
}
