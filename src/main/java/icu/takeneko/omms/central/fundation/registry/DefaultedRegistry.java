package icu.takeneko.omms.central.fundation.registry;

import org.jetbrains.annotations.Nullable;

public class DefaultedRegistry<I, T> extends MapRegistry<I, T> {
    private final T defaultValue;

    public DefaultedRegistry(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public @Nullable T get(I key) {
        T value = super.get(key);
        return value == null ? defaultValue : value;
    }
}
