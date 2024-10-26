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

public class DispatchedCodec<A> extends MapCodec<A> {
    private final Lookup<Identifier, Codec<A>> lookup;
    private final String keyName;

    public DispatchedCodec(Lookup<Identifier, Codec<A>> lookup, String keyName) {
        this.lookup = lookup;
        this.keyName = keyName;
    }


    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.empty();
    }

    @Override
    public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
        return null;
    }

    @Override
    public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        return null;
    }
}
