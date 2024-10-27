package icu.takeneko.omms.central.controller.console.ws.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;

import java.util.List;

public class WSLogPacket implements WSPacket{

    public static final MapCodec<WSLogPacket> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.STRING.listOf().fieldOf("lines").forGetter(o -> o.lines)
    ).apply(ins, WSLogPacket::new));

    private final List<String> lines;

    protected WSLogPacket(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public void handle(WSPacketHandler handler) {
        handler.onLog(lines);
    }

    @Override
    public MapCodec<? extends WSPacket> codec() {
        return CODEC;
    }
}
