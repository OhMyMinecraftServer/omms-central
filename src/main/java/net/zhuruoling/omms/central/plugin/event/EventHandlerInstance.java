package net.zhuruoling.omms.central.plugin.event;

import net.zhuruoling.omms.central.plugin.PluginMain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class EventHandlerInstance {

    String eventId;
    Map<String, Class<?>> argTypes;
    Method method;
    PluginMain instance;
    public EventResult handle(Object... args) throws InvocationTargetException, IllegalAccessException {
        return (EventResult) method.invoke(instance,args);
    }
}
