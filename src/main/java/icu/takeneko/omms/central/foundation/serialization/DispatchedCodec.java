package icu.takeneko.omms.central.foundation.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import icu.takeneko.omms.central.fundation.Lookup;

import java.util.stream.Stream;

public class DispatchedCodec<A, K> extends MapCodec<A> {
    private final Lookup<K, MapCodec<A>> lookup;
    private final Lookup<A, MapCodec<A>> codecLookup;
    private final Lookup<A, K> keyLookup;
    private final Codec<K> keyCodec;
    private final String keyName;

    public DispatchedCodec(
        Lookup<K, MapCodec<A>> lookup,
        Lookup<A, MapCodec<A>> codecLookup,
        Lookup<A, K> keyLookup,
        Codec<K> keyCodec,
        String keyName
    ) {
        this.lookup = lookup;
        this.codecLookup = codecLookup;
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
            if (codec == null){
                return DataResult.error(() -> "No matching MapCodec for decoding " + input + " with type " + it.getFirst());
            }
            if (ops.compressMaps()){
                T value = input.get("value");
                if (value == null){
                    return DataResult.error(() -> "Input does not have a \"value\" entry: " + input);
                }
                return codec.decoder().parse(ops, value);
            }
            return codec.decode(ops, input);
        });
    }

    @Override
    public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        MapCodec<A> codec = codecLookup.get(input);
        if (codec == null){
            return prefix.withErrorsFrom(DataResult.error(() -> "No matching MapCodec for encoding " + input));
        }
        if (ops.compressMaps()){
            return prefix
                .add(keyName, keyCodec.encodeStart(ops, keyLookup.get(input)))
                .add("value", codec.encoder().encodeStart(ops, input));
        }
        return codec.encode(input, ops, prefix)
            .add(keyName, keyCodec.encodeStart(ops, keyLookup.get(input)));
    }
}
