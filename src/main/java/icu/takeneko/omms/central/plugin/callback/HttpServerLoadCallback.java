package icu.takeneko.omms.central.plugin.callback;

import io.ktor.server.application.Application;

import java.util.function.Consumer;

public class HttpServerLoadCallback extends Callback<Application> {

    public static final HttpServerLoadCallback INSTANCE = new HttpServerLoadCallback();

    @Override
    public void register(Consumer<Application> consumer) {
        this.consumers.add(consumer);
    }
}
