package icu.takeneko.omms.central.plugin.callback;

import icu.takeneko.omms.central.network.chatbridge.Broadcast;

import java.util.function.Consumer;

public class ChatbridgeBroadcastReceivedCallback extends Callback<Broadcast> {

    public static ChatbridgeBroadcastReceivedCallback INSTANCE = new ChatbridgeBroadcastReceivedCallback();

    @Override
    public void register(Consumer<Broadcast> consumer) {
        this.consumers.add(consumer);
    }
}
