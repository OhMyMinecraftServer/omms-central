package icu.takeneko.omms.central.controller.console.ws;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public class WSStatusPacket implements WSPacket {

    public static final Codec<WSStatusPacket> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            PacketType.CODEC.fieldOf("packetType").forGetter(o -> o.packetType),
            Codec.INT.optionalFieldOf("version").forGetter(o -> Optional.of(o.version))
    ).apply(ins, WSStatusPacket::new));

    private final PacketType packetType;
    private final int version;

    public WSStatusPacket(PacketType packetType, Integer version) {
        this.packetType = packetType;
        this.version = version;
    }

    private WSStatusPacket(PacketType packetType, Optional<Integer> version) {
        this(packetType, version.orElse(-1));
    }

    public WSStatusPacket(PacketType packetType) {
        this.packetType = packetType;
        this.version = -1;
    }

    @Override
    public void handle(WSPacketHandler handler) {
        switch (packetType){
            case CONNECT -> handler.onConnect(version);
            case DISCONNECT -> handler.onDisconnect();
        }
    }
}
