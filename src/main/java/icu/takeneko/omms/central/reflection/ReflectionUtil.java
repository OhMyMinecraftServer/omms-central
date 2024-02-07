package icu.takeneko.omms.central.reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ReflectionUtil {
    public static @NotNull Method getMethodByName(@NotNull Object obj, @NotNull String name, Class<?>... paramTypes) throws NoSuchMethodException {
        Class<?> clazz = obj.getClass();
        return clazz.getMethod(name, paramTypes);
    }

    public Object invokeMethod(@NotNull Object instance, @NotNull String methodName, Object @NotNull ... params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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