package icu.takeneko.omms.central.plugin.callback;

import icu.takeneko.omms.central.controller.ControllerManager;

import java.util.function.Consumer;

public class ControllerLoadCallback extends Callback<ControllerManager> {

    public static final ControllerLoadCallback INSTANCE = new ControllerLoadCallback();

    @Override
    public void register(Consumer<ControllerManager> consumer) {
        this.consumers.add(consumer);
    }
}
