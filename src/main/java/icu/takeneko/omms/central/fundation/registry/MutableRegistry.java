package icu.takeneko.omms.central.fundation.registry;

public interface MutableRegistry<I, T> extends Registry<I, T> {

    void register(I key, T value);

    void unregister(I key);

    default Registry<I, T> toImmutableRegistry() {
        return this;
    }
}
