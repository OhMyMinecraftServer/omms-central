package net.zhuruoling.omms.central.controller.console.output;

import java.util.Stack;

public class SessionInputSource extends InputSource{

    final Stack<String> cache = new Stack<>();

    @Override
    public String getLine() {
        if (!cache.isEmpty()) {
            synchronized (cache) {
                return cache.pop();
            }
        }else {
            return null;
        }
    }

    public void put(String item){
        synchronized (cache){
            cache.push(item);
        }
    }
}
