package icu.takeneko.omms.central.fundation.registry;

import icu.takeneko.omms.central.fundation.Lookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Registry<I, T> extends Lookup<I, T> {

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
