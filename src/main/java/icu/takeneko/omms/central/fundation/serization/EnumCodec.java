package icu.takeneko.omms.central.fundation.serization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public abstract class EnumCodec<E extends Enum<E> & SerializableEnum> implements Codec<E> {
    private final Codec<E> elementCodec;

    public static <E extends Enum<E> & SerializableEnum> EnumCodec<E> of(Function<String, E> resolver) {
        return new EnumCodec(){
            @Nullable
            @Override
            public E byName(@Nullable String name) {
                return resolver.apply(name);
            }
        };
    }

    public EnumCodec() {
        this.elementCodec = Codec.STRING.flatXmap(s -> {
            E elem = this.byName(s);
            if (elem == null) {
                return DataResult.error(() -> "Unknown name for E: " + s);
            }
            return DataResult.success(elem);
        }, o -> {
            String name = o.getSerializedName();
            return DataResult.success(name);
        });
    }

    @Override
    public final <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
        return elementCodec.decode(ops, input);
    }

    @Override
    public final <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
        return elementCodec.encode(input, ops, prefix);
    }

    @Nullable
    public abstract E byName(@Nullable String name);

    public E byName(@Nullable String name, E defaultValue) {
        return Objects.requireNonNullElse(this.byName(name), defaultValue);
    }
}
