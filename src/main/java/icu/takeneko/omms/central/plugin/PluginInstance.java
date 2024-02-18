package icu.takeneko.omms.central.plugin;

import icu.takeneko.omms.central.plugin.depedency.PluginDependency;
import icu.takeneko.omms.central.plugin.handler.PluginRequestHandler;
import icu.takeneko.omms.central.plugin.metadata.PluginDependencyRequirement;
import icu.takeneko.omms.central.plugin.metadata.PluginMetadata;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipFile;

@SuppressWarnings("all")
public class PluginInstance {
    private PluginMetadata pluginMetadata;
    private final Logger logger = LoggerFactory.getLogger("PluginInstance");
    private Class<?> pluginMainClass;
    private @NotNull PluginState pluginState = PluginState.NO_STATE;
    private PluginMain pluginMain;
    private final Map<String, PluginRequestHandler> pluginRequestHandlers = new LinkedHashMap<>();
    private final @NotNull URL pluginPathUrl;
    private final @NotNull Path pluginPath;
    private final JarClassLoader classLoader;


    public PluginInstance(JarClassLoader classLoader, @NotNull Path jarPath) throws MalformedURLException {
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
            if (Objects.isNull(pluginMetadata.getId())) {
                logger.error("This plugin metadata file has no pluginId specified,file path: %s".formatted(pluginPath.toAbsolutePath().toString()));
                pluginState = PluginState.ERROR;
                return;
            }
            if (pluginMetadata.getPluginDependencies() != null) {
                pluginMetadata.getPluginDependencies().forEach(requirement -> {
                    requirement.parseRequirement();
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pluginState = PluginState.PRE_LOAD;
    }

    public void loadPluginDepedencies(Map<PluginDependency, Path> pathMap) {

    }

    public void loadPluginClasses() {
        if (pluginMetadata.getPluginMainClass() != null) {
            logger.debug("Plugin main class name: " + pluginMetadata.getPluginMainClass());
            try {
                pluginMainClass = classLoader.loadClass(pluginMetadata.getPluginMainClass());
                pluginMain = (PluginMain) pluginMainClass.getConstructor().newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                logger.error("Cannot find plugin main class %s".formatted(pluginMetadata.getPluginMainClass()), e);
                pluginState = PluginState.ERROR;
            } catch (ClassCastException e) {
                logger.error("Cannot cast plugin main class %s to PluginMain.".formatted(pluginMetadata.getPluginMainClass()), e);
                pluginState = PluginState.ERROR;
            }
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
                        pluginState = PluginState.ERROR;
                        return new Pair<PluginRequestHandler, String>(null, null);
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("Request handler class %s not exist.".formatted(s), e);
                    pluginState = PluginState.ERROR;
                    return new Pair<PluginRequestHandler, String>(null, null);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    logger.error("Class %s is not a valid request handler.".formatted(s), e);
                    pluginState = PluginState.ERROR;
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
                    pluginState = PluginState.ERROR;
                    return;
                }
                pluginRequestHandlers.put(it.component2(), it.component1());
            });
        } else {
            logger.debug("Plugin %s has no RequestHandler".formatted(pluginPath.toAbsolutePath().toString()));
        }
    }


    public @NotNull List<PluginDependencyRequirement> checkDepenciesSatisfied(@NotNull List<PluginDependency> dependencies) {
        return pluginMetadata.getPluginDependencies().stream()
                .filter(requirement -> dependencies.stream().noneMatch(it -> requirement.requirementMatches(it)))
                .toList();
    }

    public @NotNull List<PluginDependencyRequirement> checkPluginDependcencyRequirements(@NotNull List<PluginDependency> dependencies) {
        pluginState = PluginState.ERROR;
        List<PluginDependencyRequirement> result = new ArrayList<PluginDependencyRequirement>();
        if (pluginMetadata.getPluginDependencies() != null) {
            result = pluginMetadata.getPluginDependencies().stream().filter(it -> dependencies.stream().noneMatch(it2 -> it.requirementMatches(it2))).toList();
        }
        pluginState = PluginState.INITIALIZED;
        return result;
    }


    public void onInitialize() {
        pluginMain.onInitialize();
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return classLoader.loadClass(className);
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

    public @NotNull Map<String, PluginRequestHandler> getPluginRequestHandlers() {
        return pluginRequestHandlers;
    }


}
