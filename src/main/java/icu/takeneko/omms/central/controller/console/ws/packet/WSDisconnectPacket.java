package icu.takeneko.omms.central.controller.console.ws.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;

public class WSDisconnectPacket implements WSPacket{

    public static final MapCodec<WSDisconnectPacket> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.INT.fieldOf("reserved").forGetter(o -> o.unused)
    ).apply(ins, WSDisconnectPacket::new));
    private final int unused = 0;

    WSDisconnectPacket(int unused){
        this();
    }

    public WSDisconnectPacket() {
    }

    @Override
    public void handle(WSPacketHandler handler) {
    }

    @Override
    public MapCodec<? extends WSPacket> codec() {
        return CODEC;
    }
}
