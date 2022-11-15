package net.zhuruoling.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ReflectionUtil {
    public static Method getMethodByName(Object obj, String name, Class<?>... paramTypes) throws NoSuchMethodException {
        Class<?> clazz = obj.getClass();
        return clazz.getMethod(name, paramTypes);
    }

    public Object invokeMethod(Object instance, String methodName, Object... params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = instance.getClass();
        ArrayList<Class<?>> paramTypes = new ArrayList<>();
        for (Object param : params) {
            paramTypes.add(param.getClass());
        }
        var method = clazz.getMethod(methodName, paramTypes.toArray(new Class<?>[]{}));
        method.setAccessible(true);
        return method.invoke(instance, params);
    }
}
