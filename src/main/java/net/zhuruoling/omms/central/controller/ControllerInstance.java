package net.zhuruoling.omms.central.controller;

import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

public record ControllerInstance(Controller controller, ControllerTypes controllerType) {
    @Override
    public @NotNull String toString() {
        return "ControllerInstance{" +
                "controller=" + controller.toString() +
                ", controllerTypes=" + controllerType.name() +
                '}';
    }

    public String toJson(){
        return Util.gson.toJson(this);
    }
}
