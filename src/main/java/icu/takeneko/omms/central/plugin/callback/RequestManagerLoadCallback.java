package icu.takeneko.omms.central.plugin.callback;

import icu.takeneko.omms.central.network.session.request.RequestManager;

import java.util.function.Consumer;

public class RequestManagerLoadCallback extends Callback<RequestManager> {

    public static final RequestManagerLoadCallback INSTANCE = new RequestManagerLoadCallback();

    @Override
    public void register(Consumer<RequestManager> consumer) {
        this.consumers.add(consumer);
    }
}
