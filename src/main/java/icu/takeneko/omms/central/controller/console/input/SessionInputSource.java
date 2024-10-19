package icu.takeneko.omms.central.controller.console.input;

import icu.takeneko.omms.central.controller.console.ControllerConsole;

import java.util.Stack;

public class SessionInputSource extends InputSource {

    final Stack<String> cache = new Stack<>();

    public SessionInputSource(ControllerConsole console) {
        super(console);
    }

    @Override
    public String getLine() {
        if (!cache.isEmpty()) {
            synchronized (cache) {
                return cache.pop();
            }
        } else {
            return "";
        }
    }

    public void put(String item) {
        synchronized (cache) {
            cache.push(item);
        }
    }
}
