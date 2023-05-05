package net.zhuruoling.omms.central.plugin;

import kotlin.Pair;
import net.zhuruoling.omms.central.plugin.annotations.EventHandler;
import net.zhuruoling.omms.central.plugin.event.EventHandlerInstance;
import net.zhuruoling.omms.central.plugin.handler.PluginRequestHandler;
import net.zhuruoling.omms.central.plugin.metadata.PluginMetadata;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

import org.slf4j.Logger;

public class PluginInstance implements AutoCloseable {
    private PluginMetadata pluginMetadata;

    private final Logger logger = LoggerFactory.getLogger("PluginInstance");

    private Class<?> pluginMainClass;
    private PluginState pluginState = PluginState.NO_STATE;
    private PluginMain pluginMain;
    private final List<EventHandlerInstance> eventHandlers = new ArrayList<>();
    private final Map<String, PluginRequestHandler> pluginRequestHandlers = new LinkedHashMap<>();
    private final URL pluginPathUrl;

    private final Path pluginPath;
    private URLClassLoader classLoader;


    public PluginInstance(Path jarPath) throws MalformedURLException {
        pluginPathUrl = new URL("file://" + jarPath.toAbsolutePath());
        pluginPath = jarPath;
    }

    public void loadJar() {
        logger.info("Loading Plugin: " + pluginPath.getFileName());
        pluginState = PluginState.NO_STATE;
        try {
            classLoader = new URLClassLoader(new URL[]{pluginPathUrl});
            var stream = classLoader.getResourceAsStream("plugin.metadata.json");
            if (stream == null) {
                logger.error("This plugin file has no plugin metadata file,file path: %s".formatted(pluginPath.toAbsolutePath().toString()));
                pluginState = PluginState.ERROR;
                return;
            }
            var pluginMetadataString = new String(stream.readAllBytes(), Charset.defaultCharset());
            logger.debug("Plugin metadata: " + pluginMetadataString);
            pluginMetadata = PluginMetadata.fromJson(pluginMetadataString);
            if (pluginMetadata.getPluginMainClass() != null) {
                logger.debug("Plugin main class name: " + pluginMetadata.getPluginMainClass());
                pluginMainClass = classLoader.loadClass(pluginMetadata.getPluginMainClass());
                pluginMain = (PluginMain) pluginMainClass.getConstructor().newInstance();
            }
            if (pluginMetadata.getPluginEventHandlers() != null) {
                for (String pluginEventHandler : pluginMetadata.getPluginEventHandlers()) {
                    logger.debug("Plugin %s -> EventHandler CLASS %s".formatted(pluginMetadata.getId(), pluginEventHandler));
                    try {
                        var clazz = classLoader.loadClass(pluginEventHandler);
                        Arrays.stream(clazz.getDeclaredMethods()).filter(it ->
                                Arrays.stream(it.getAnnotations()).anyMatch(it2 -> it2.annotationType() == EventHandler.class)
                        ).forEach(it -> {

                        });
                    } catch (ClassNotFoundException e) {
                        logger.error("Event Handler %s not exist.".formatted(pluginEventHandler), e);
                    }
                }
            } else {
                logger.debug("");
            }
            if (pluginMetadata.getPluginRequestHandlers() != null) {
                Arrays.stream(pluginMetadata.getPluginRequestHandlers()).map(s -> {
                    logger.debug("Plugin %s -> RequestHandler CLASS %s".formatted(pluginMetadata.getId(), s));
                    try {
                        var clazz = classLoader.loadClass(s);
                        if (clazz.getSuperclass() == PluginRequestHandler.class) {
                            var constructor = clazz.getConstructor();
                            constructor.setAccessible(true);
                            PluginRequestHandler requestHandler = (PluginRequestHandler) constructor.newInstance();
                            logger.debug("Plugin %s -> RequestHandler %s by %s".formatted(pluginMetadata.getId(), requestHandler.getRequestCode(), s));
                            return new Pair<>(requestHandler, requestHandler.getRequestCode());
                        } else {
                            logger.error("Class %s is not a valid request handler.".formatted(s));
                            return new Pair<PluginRequestHandler, String>(null, null);
                        }
                    } catch (ClassNotFoundException e) {
                        logger.error("Request handler class %s not exist.".formatted(s), e);
                        return new Pair<PluginRequestHandler, String>(null, null);
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                             IllegalAccessException e) {
                        logger.error("Class %s is not a valid request handler.".formatted(s), e);
                        return new Pair<PluginRequestHandler, String>(null, null);
                    }

                }).forEach(it -> {
                    if (it.component1() == null || it.component2() == null) {
                        return;
                    }
                    if (pluginRequestHandlers.containsKey(it.component2())) {
                        logger.error("Plugin request handler %s has a same request code with %s,which is NOT ALLOWED."
                                .formatted(it.component1().getClass().getName(),
                                        pluginRequestHandlers.get(it.component2()).getClass().getName()));
                        return;
                    }
                    pluginRequestHandlers.put(it.component2(), it.component1());
                });
            } else {

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pluginState = PluginState.INITIATED;
    }

    public void onLoad() {
        pluginMain.onLoad();
    }

    public void onUnload() {
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

    public PluginState getPluginState() {
        return pluginState;
    }

    public List<EventHandlerInstance> getEventHandlers() {
        return eventHandlers;
    }

    public Map<String, PluginRequestHandler> getPluginRequestHandlers() {
        return pluginRequestHandlers;
    }
}
