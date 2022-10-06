package net.zhuruoling.controller;

import net.zhuruoling.util.Util;

public record ControllerInstance(Controller controller, ControllerTypes controllerType) {
    @Override
    public String toString() {
        return "ControllerInstance{" +
                "controller=" + controller.toString() +
                ", controllerTypes=" + controllerType.name() +
                '}';
    }

    public String toJson(){
        return Util.gson.toJson(this);
    }
}
