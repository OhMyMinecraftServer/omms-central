package icu.takeneko.omms.central.controller.console;

import icu.takeneko.omms.central.controller.Controller;
import icu.takeneko.omms.central.controller.console.input.InputSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ControllerConsole {
    Controller getController();

    String getConsoleId();

    void start();

    boolean isAlive();

    InputSource getInputSource();

    void close();

    CompletableFuture<List<String>> complete(String input, int cursorPos);
}
