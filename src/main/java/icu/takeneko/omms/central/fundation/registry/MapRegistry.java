package icu.takeneko.omms.central.fundation.registry;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MapRegistry<I, T> implements FreezableRegistry<I, T> {
    protected final Map<I, T> map = new HashMap<>();
    protected final Map<T, I> reversedMap = new HashMap<>();
    @Getter
    private boolean frozen = false;

    @Override
    public void frozen() {
        frozen = true;
    }

    @Override
    public void register(I key, T value) {
        synchronized (map){
            checkFrozen();
            map.put(key, value);
            reversedMap.put(value, key);
        }
    }

    private void checkFrozen() {
        if (frozen) throw new IllegalStateException("Registry already frozen");
    }

    @Override
    public void unregister(I key) {
        checkFrozen();
        T value = map.remove(key);
        if (value == null) return;
        reversedMap.remove(value);
    }

    @Override
    public @Nullable T get(I key) {
        synchronized (map){
            return map.get(key);
        }
    }

    @Override
    @Nullable
    public I getKey(T entry) {
        synchronized (reversedMap) {
            return reversedMap.get(entry);
        }
    }
}
