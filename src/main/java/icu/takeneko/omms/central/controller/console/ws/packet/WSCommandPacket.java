package icu.takeneko.omms.central.controller.console.ws.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;

public class WSCommandPacket implements WSPacket {

    public static final MapCodec<WSCommandPacket> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.STRING.fieldOf("command").forGetter(o -> o.command)
    ).apply(ins, WSCommandPacket::new));

    private final String command;

    public WSCommandPacket(String command) {
        this.command = command;
    }

    @Override
    public void handle(WSPacketHandler handler) {
    }

    @Override
    public MapCodec<? extends WSPacket> codec() {
        return CODEC;
    }
}
