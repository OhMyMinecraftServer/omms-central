package icu.takeneko.omms.central.plugin.callback;

import icu.takeneko.omms.central.network.session.request.RequestHandlerManager;

import java.util.function.Consumer;

public class RequestManagerLoadCallback extends Callback<RequestHandlerManager> {

    public static final RequestManagerLoadCallback INSTANCE = new RequestManagerLoadCallback();

    @Override
    public void register(Consumer<RequestHandlerManager> consumer) {
        this.consumers.add(consumer);
    }
}
