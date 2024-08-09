package icu.takeneko.omms.central.controller.console.ws;

import com.mojang.serialization.Codec;
import icu.takeneko.omms.central.fundation.serization.EnumCodec;
import icu.takeneko.omms.central.fundation.serization.SerializableEnum;
import org.jetbrains.annotations.NotNull;

public enum PacketType implements SerializableEnum {
    CONNECT, DISCONNECT,
    COMMAND, LOG;

    public static final Codec<PacketType> CODEC = EnumCodec.of(PacketType::valueOf);

    @Override
    @NotNull
    public String getSerializedName() {
        return name();
    }
}