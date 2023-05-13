package net.zhuruoling.omms.central.plugin.callback;

import net.zhuruoling.omms.central.controller.ControllerManager;

import java.util.function.Consumer;

public class ControllerLoadCallback extends Callback<ControllerManager> {

    public static final ControllerLoadCallback INSTANCE = new ControllerLoadCallback();

    @Override
    void register(Consumer<ControllerManager> consumer) {
        this.consumers.add(consumer);
    }
}
