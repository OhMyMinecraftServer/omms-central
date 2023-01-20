package net.zhuruoling.omms.central.plugin;

import groovy.lang.GroovyClassLoader;
import net.zhuruoling.omms.central.main.RuntimeConstants;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class GroovyPluginInstance {
    private final String pluginFilePath;
    private final @NotNull GroovyClassLoader groovyClassLoader;
    private @Nullable PluginMain instance = null;
    private PluginStatus pluginStatus = PluginStatus.NONE;
    private @Nullable PluginMetadata metadata = null;

    public GroovyPluginInstance(String pluginFilePath) {
        this.pluginFilePath = pluginFilePath;
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
    }

    public String getPluginFilePath() {
        return pluginFilePath;
    }

    public PluginStatus getPluginStatus() {
        return pluginStatus;
    }

    public void setPluginStatus(PluginStatus pluginStatus) {
        this.pluginStatus = pluginStatus;
    }

    public PluginMetadata getMetadata() {
        return metadata;
    }

    public void initPlugin() {
        if (!Files.exists(Path.of(pluginFilePath))) {
            throw new PluginNotExistException("The specified plugin file %s does not exist.".formatted(pluginFilePath));
        }
        try {
            Class<?> clazz = groovyClassLoader.parseClass(new File(pluginFilePath));
            instance = (PluginMain) clazz.getDeclaredConstructor().newInstance();
            this.metadata = instance.getPluginMetadata();
            var map = new HashMap<String, Method>();
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                declaredMethod.setAccessible(true);
                for (Annotation declaredAnnotation : declaredMethod.getDeclaredAnnotations()) {
                    if (declaredAnnotation.annotationType() == Api.class) {
                        String name = declaredMethod.getName();
                        System.out.printf("Plugin %s got Api Method %s%n", metadata.id, name);
                        map.put(name, declaredMethod);
                    }
                }
            }
            RuntimeConstants.INSTANCE.getPluginDeclaredApiMethod().put(getMetadata().id, map);
        } catch (MultipleCompilationErrorsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public @Nullable Object invokeMethod(@NotNull String methodName, Object @NotNull ... params) {
        Class<? extends PluginMain> clazz = instance.getClass();
        ArrayList<Class<?>> paramTypes = new ArrayList<>();
        for (Object param : params) {
            paramTypes.add(param.getClass());
        }
        try {
            var method = clazz.getMethod(methodName, paramTypes.toArray(new Class<?>[]{}));
            method.setAccessible(true);
            return method.invoke(instance, params);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        //return object.invokeMethod(methodName, params);
    }

    public void onLoad(LifecycleServerInterface lifecycleServerInterface) {
        instance.onLoad(lifecycleServerInterface);
    }

    public void onUnload(LifecycleServerInterface lifecycleServerInterface) {
        instance.onUnload(lifecycleServerInterface);
    }


    public PluginMain getInstance() {
        return instance;
    }
}
