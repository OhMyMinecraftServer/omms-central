package net.zhuruoling.omms.central.api.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("all")
public class ConfigLoader {
    public static <T> @Nullable T loadConfig(@NotNull Path configPath,
                                             @NotNull Class<? extends T> configClass,
                                             T defaultConfig,
                                             @NotNull ExclusionStrategy exclusionStrategy,
                                             Map<Class<?>, TypeAdapter<?>> typeAdapterMap
    ) {
        var fields = configClass.getDeclaredFields();
        var map = new HashMap<Class<?>, WrappedConfigValue>();
        if (!configPath.toFile().exists()) {
            try {
                configPath.toFile().createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (var reader = new BufferedReader(new FileReader(configPath.toFile()))) {
            Properties properties = new Properties();
            properties.load(reader);
            for (Field field : fields) {
                if (exclusionStrategy.exclude(field.getType())) continue;
                map.put(field.getDeclaringClass(), new WrappedConfigValue(null));
                var name = field.getName();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private record WrappedConfigValue(@Nullable Object value) {
    }
}
