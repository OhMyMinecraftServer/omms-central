package icu.takeneko.omms.central.controller.console.input;

import icu.takeneko.omms.central.controller.console.ControllerConsole;

abstract public class InputSource {
    protected final ControllerConsole console;

    public InputSource(ControllerConsole console) {
        this.console = console;
    }

    public abstract String getLine();

    @FunctionalInterface
    public interface InputSourceFactory {
        InputSource create(ControllerConsole console);
    }
}
