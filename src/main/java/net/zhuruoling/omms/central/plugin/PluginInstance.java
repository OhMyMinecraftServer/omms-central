package net.zhuruoling.omms.central.plugin;

import kotlin.Pair;
import net.zhuruoling.omms.central.plugin.depedency.PluginDependency;
import net.zhuruoling.omms.central.plugin.handler.PluginRequestHandler;
import net.zhuruoling.omms.central.plugin.metadata.PluginDependencyRequirement;
import net.zhuruoling.omms.central.plugin.metadata.PluginMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;
@SuppressWarnings("all")
public class PluginInstance implements AutoCloseable {
    private PluginMetadata pluginMetadata;
    private final Logger logger = LoggerFactory.getLogger("PluginInstance");
    private Class<?> pluginMainClass;
    private PluginState pluginState = PluginState.NO_STATE;
    private PluginMain pluginMain;
    private final Map<String, PluginRequestHandler> pluginRequestHandlers = new LinkedHashMap<>();
    private final URL pluginPathUrl;
    private final Path pluginPath;
    private final URLClassLoader classLoader;


    public PluginInstance(URLClassLoader classLoader, Path jarPath) throws MalformedURLException {
        pluginPathUrl = new URL("file://" + jarPath.toAbsolutePath());
        pluginPath = jarPath;
        this.classLoader = classLoader;
    }

    public void loadJar() {
        logger.info("Loading Plugin: " + pluginPath.getFileName());
        pluginState = PluginState.NO_STATE;
        try (ZipFile zipFile = new ZipFile(pluginPath.toFile())) {
            var entry = zipFile.getEntry("plugin.metadata.json");
            if (entry == null) {
                logger.error("This plugin file has no plugin metadata file,file path: %s".formatted(pluginPath.toAbsolutePath().toString()));
                pluginState = PluginState.ERROR;
                return;
            }
            var stream = zipFile.getInputStream(entry);
            var pluginMetadataString = new String(stream.readAllBytes(), Charset.defaultCharset());
            logger.debug("Plugin metadata: " + pluginMetadataString);
            pluginMetadata = PluginMetadata.fromJson(pluginMetadataString);
            pluginMetadata.getPluginDependencies().forEach(requirement -> requirement.parseRequirement());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pluginState = PluginState.PRE_LOAD;
    }

    public void loadPluginDepedencies(Map<PluginDependency, Path> pathMap) {

    }

    public void loadPluginClasses() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (pluginMetadata.getPluginMainClass() != null) {
            logger.debug("Plugin main class name: " + pluginMetadata.getPluginMainClass());
            pluginMainClass = classLoader.loadClass(pluginMetadata.getPluginMainClass());
            pluginMain = (PluginMain) pluginMainClass.getConstructor().newInstance();
        }
        if (pluginMetadata.getPluginRequestHandlers() != null) {
            pluginMetadata.getPluginRequestHandlers().stream().map(s -> {
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
            logger.debug("Plugin %s has no RequestHandler".formatted(pluginPath.toAbsolutePath().toString()));
        }
    }


    public List<PluginDependencyRequirement> checkDepenciesSatisfied(List<PluginDependency> dependencies) {
        return pluginMetadata.getPluginDependencies().stream()
                .filter(requirement -> dependencies.stream().noneMatch(it -> requirement.requirementMatches(it)))
                .toList();
    }

    public void onInitialize() {
        pluginMain.onInitialize();
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return classLoader.loadClass(className);
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

    public Map<String, PluginRequestHandler> getPluginRequestHandlers() {
        return pluginRequestHandlers;
    }
}
