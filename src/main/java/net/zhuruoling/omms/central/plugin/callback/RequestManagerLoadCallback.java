package net.zhuruoling.omms.central.plugin.callback;

import net.zhuruoling.omms.central.network.session.request.RequestManager;

import java.util.function.Consumer;

public class RequestManagerLoadCallback extends Callback<RequestManager> {

    public final RequestManagerLoadCallback INSTANCE = new RequestManagerLoadCallback();

    @Override
    void register(Consumer<RequestManager> consumer) {
        this.consumers.add(consumer);
    }
}
