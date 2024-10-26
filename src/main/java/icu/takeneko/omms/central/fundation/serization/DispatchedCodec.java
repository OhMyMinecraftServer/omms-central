package icu.takeneko.omms.central.fundation.serization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import icu.takeneko.omms.central.fundation.Lookup;
import icu.takeneko.omms.central.fundation.registry.Identifier;

import java.util.stream.Stream;

public class DispatchedCodec<A, K> extends MapCodec<A> {
    private final Lookup<K, MapCodec<A>> lookup;
    private final Lookup<A, DataResult<K>> keyLookup;
    private final Codec<K> keyCodec;
    private final String keyName;

    public DispatchedCodec(
        Lookup<K, MapCodec<A>> lookup,
        Lookup<A, DataResult<K>> keyLookup,
        Codec<K> keyCodec,
        String keyName
    ) {
        this.lookup = lookup;
        this.keyLookup = keyLookup;
        this.keyCodec = keyCodec;
        this.keyName = keyName;
    }


    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.of(keyName, "value").map(ops::createString);
    }

    @Override
    public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
        T key = input.get(keyName);
        if (key == null){
            return DataResult.error(() -> "No key \"" + keyName + "\" in " + input);
        }

        return keyCodec.decode(ops, key).flatMap(it -> {
            MapCodec<A> codec = lookup.get(it.getFirst());
            return codec.decode(ops, input);
        });
    }

    @Override
    public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        DataResult<K> keyResult = keyLookup.get(input);
        if (keyResult.isError()){
            return prefix.withErrorsFrom(keyResult);
        }
        K key = keyResult.getOrThrow();
        MapCodec<A> codec = lookup.get(key);
        if (codec == null){
            return prefix.withErrorsFrom(DataResult.error(() -> "No matching candidate MapCodec for encoding " + input));
        }
        if (ops.compressMaps()){
            return prefix
                .add(keyName, keyResult.flatMap(it -> keyCodec.encodeStart(ops, it)))
                .add("value", codec.encoder().encodeStart(ops, input));
        }
        return codec.encode(input, ops, prefix)
            .add(keyName, keyResult.flatMap(it -> keyCodec.encodeStart(ops, it)));
    }
}
