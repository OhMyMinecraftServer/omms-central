package net.zhuruoling.plugin;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class GroovyPluginInstance {
    private final String pluginFilePath;
    private final GroovyClassLoader groovyClassLoader;
    private PluginMain instance = null;
    private PluginStatus pluginStatus = PluginStatus.NONE;
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


    private PluginMetadata metadata = null;

    public GroovyPluginInstance(String pluginFilePath) {
        this.pluginFilePath = pluginFilePath;
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
    }

    public void initPlugin(){
        if (!Files.exists(Path.of(pluginFilePath))){
            throw new PluginNotExistException("The specified plugin file %s does not exist.".formatted(pluginFilePath));
        }
        try {
            Class<?> groovyClass = groovyClassLoader.parseClass(new File(pluginFilePath));
            instance = (PluginMain) groovyClass.getDeclaredConstructor().newInstance();
            //System.out.println(something.getPluginMetadata().toString());
            this.metadata = instance.getPluginMetadata();
        }
        catch (MultipleCompilationErrorsException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object invokeMethod(String methodName, Object... params){
        var clazz = instance.getClass();
        ArrayList<Class<?>> paramTypes = new ArrayList<>();
        for (Object param : params) {
            paramTypes.add(param.getClass());
        }
        try {
            var method = clazz.getMethod(methodName, paramTypes.toArray(new  Class<?>[]{}));
            method.setAccessible(true);
            return method.invoke(instance,params);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        //return object.invokeMethod(methodName, params);
    }

    public void onLoad(LifecycleServerInterface lifecycleServerInterface){
        instance.onLoad(lifecycleServerInterface);
    }

    public void onUnload(LifecycleServerInterface lifecycleServerInterface){
        instance.onUnload(lifecycleServerInterface);
    }


}
