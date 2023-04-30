package net.zhuruoling.omms.central.plugin;

import net.zhuruoling.omms.central.plugin.metadata.PluginMetadata;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.slf4j.Logger;

public class PluginInstance implements AutoCloseable {
    private PluginMetadata pluginMetadata;

    private final Logger logger = LoggerFactory.getLogger("PluginInstance");

    private Class<?> pluginMainClass;

    private PluginMain pluginMain;

    private final URL pluginPathUrl;

    private final Path pluginPath;
    private URLClassLoader classLoader;

    public PluginInstance(Path jarPath) throws MalformedURLException {
        pluginPathUrl = new URL("file://" + jarPath.toAbsolutePath());
        pluginPath = jarPath;
    }

    public void loadJar() {
        logger.info("Loading Plugin: " + pluginPath.getFileName());
        try {
            classLoader = new URLClassLoader(new URL[]{pluginPathUrl});
            var stream = classLoader.getResourceAsStream("plugin.metadata.json");
            var pluginMetadataString = new String(stream.readAllBytes(), Charset.defaultCharset());
            logger.debug("Plugin metadata: " + pluginMetadataString);
            pluginMetadata = PluginMetadata.fromJson(pluginMetadataString);
            logger.debug("Plugin main class name: " + pluginMetadata.getPluginMainClass());
            pluginMainClass = classLoader.loadClass(pluginMetadata.getPluginMainClass());
            pluginMain = (PluginMain) pluginMainClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onLoad(){
        pluginMain.onLoad();
    }

    public void onUnload(){
        pluginMain.onUnload();
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return classLoader.loadClass(className);
    }

    public PluginInstance(String p) throws MalformedURLException {
        this(Path.of(p));
    }

    @Override
    public void close() throws Exception {
        classLoader.close();
    }

    public PluginMetadata getPluginMetadata() {
        return pluginMetadata;
    }

    public PluginMain getPluginMain() {
        return pluginMain;
    }

    public Class<?> getPluginMainClass() {
        return pluginMainClass;
    }

    public URL getPluginPathUrl() {
        return pluginPathUrl;
    }
}
