package net.zhuruoling.omms.central.script;

import groovy.lang.GroovyClassLoader;
import net.zhuruoling.omms.central.GlobalVariable;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class GroovyScriptInstance {
    private final String pluginFilePath;
    private final @NotNull GroovyClassLoader groovyClassLoader;
    private @Nullable ScriptMain instance = null;
    private ScriptStatus scriptStatus = ScriptStatus.NONE;
    private @Nullable ScriptMetadata metadata = null;

    private final Logger logger = LoggerFactory.getLogger("PluginLoader");

    public GroovyScriptInstance(String pluginFilePath) {
        this.pluginFilePath = pluginFilePath;
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
    }

    public String getPluginFilePath() {
        return pluginFilePath;
    }

    public ScriptStatus getPluginStatus() {
        return scriptStatus;
    }

    public void setPluginStatus(ScriptStatus scriptStatus) {
        this.scriptStatus = scriptStatus;
    }

    public ScriptMetadata getMetadata() {
        return metadata;
    }

    public void initPlugin() {
        if (!Files.exists(Path.of(pluginFilePath))) {
            throw new ScriptNotExistException("The specified plugin file %s does not exist.".formatted(pluginFilePath));
        }
        try {
            Class<?> clazz = groovyClassLoader.parseClass(new File(pluginFilePath));
            instance = (ScriptMain) clazz.getDeclaredConstructor().newInstance();
            this.metadata = instance.getPluginMetadata();
            if (this.metadata == null){
                throw new ScriptException("Plugin %s does not provide a plugin metadata.");
            }
            var map = new HashMap<String, Method>();
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                declaredMethod.setAccessible(true);
                for (Annotation declaredAnnotation : declaredMethod.getDeclaredAnnotations()) {
                    if (declaredAnnotation.annotationType() == Api.class) {
                        String name = declaredMethod.getName();
                        logger.debug("Plugin %s got Api Method %s%n".formatted(metadata.id, name));
                        map.put(name, declaredMethod);
                    }
                }
            }
            GlobalVariable.INSTANCE.getPluginDeclaredApiMethod().put(metadata.id, map);
        } catch (MultipleCompilationErrorsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public @Nullable Object invokeMethod(@NotNull String methodName, Object @NotNull ... params) {
        Class<? extends ScriptMain> clazz = instance.getClass();
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

    public void onLoad(LifecycleOperationInterface lifecycleServerInterface) {
        instance.onLoad(lifecycleServerInterface);
    }

    public void onUnload(LifecycleOperationInterface lifecycleServerInterface) {
        assert instance != null;
        instance.onUnload(lifecycleServerInterface);
    }


    public @Nullable ScriptMain getInstance() {
        return instance;
    }
}
