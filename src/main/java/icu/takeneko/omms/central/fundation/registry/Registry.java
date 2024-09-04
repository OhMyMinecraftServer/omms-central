package icu.takeneko.omms.central.fundation.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Registry<I, T> {

    @Nullable
    T get(I key);

    @NotNull
    default T getOrThrow(I key){
        T t = get(key);
        if (t == null) {
            throw new IllegalArgumentException("Could not get value for " + key + " as it does not exist in current registry.");
        }
        return t;
    }

    @Nullable
    I getKey(T entry);
}
