package net.zhuruoling.plugin;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class GroovyPluginInstance {
    private final String pluginFilePath;
    private final GroovyClassLoader groovyClassLoader;
    private GroovyObject object = null;

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
            object = (GroovyObject) groovyClass.getDeclaredConstructor().newInstance();
            this.metadata = (PluginMetadata) object.invokeMethod("getPluginMetadata", null);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Object invokeMethod(String methodName, Object... params){
        return object.invokeMethod(methodName, params);
    }
}
