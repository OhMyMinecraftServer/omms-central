package net.zhuruoling.controller;

public record ControllerInstance(Controller controller, ControllerTypes controllerType) {
    @Override
    public String toString() {
        return "ControllerInstance{" +
                "controller=" + controller.toString() +
                ", controllerTypes=" + controllerType.name() +
                '}';
    }
}
