package net.zhuruoling.plugin;

import com.google.gson.Gson;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import net.zhuruoling.util.PluginNotExistException;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class GroovyPluginInstance {
    private final String pluginFilePath;
    private GroovyClassLoader groovyClassLoader = null;
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

    public GroovyPluginInstance(String pluginFilePath) {
        this.pluginFilePath = pluginFilePath;
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
    }

    public PluginMetadata initPlugin(){
        if (!Files.exists(Path.of(pluginFilePath))){
            throw new PluginNotExistException("The specified plugin file %s does not exist.".formatted(pluginFilePath));
        }
        try {
            Class<?> groovyClass = groovyClassLoader.parseClass(new File(pluginFilePath));
            object = (GroovyObject) groovyClass.getDeclaredConstructor().newInstance();
            String metadataString = (String) object.getProperty("metadata");
            return new Gson().fromJson(metadataString, PluginMetadata.class);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Object invokeMethod(String methodName, Object... params){
        return object.invokeMethod(methodName, params);
    }
}
