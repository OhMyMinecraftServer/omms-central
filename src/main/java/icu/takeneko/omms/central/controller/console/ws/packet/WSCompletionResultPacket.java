package icu.takeneko.omms.central.controller.console.ws.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;
import icu.takeneko.omms.central.util.Util;

import java.util.List;

public class WSCompletionResultPacket implements WSPacket{

    public static final MapCodec<WSCompletionResultPacket> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.STRING.fieldOf("requestId").forGetter(o -> o.requestId),
            Codec.STRING.listOf().fieldOf("results").forGetter(o -> o.results)
    ).apply(ins, WSCompletionResultPacket::new));

    private final String requestId;
    private final List<String> results;

    public WSCompletionResultPacket(String requestId, List<String> results) {
        this.requestId = requestId;
        this.results = results;
    }

    @Override
    public void handle(WSPacketHandler handler) {
        handler.onCompletionResult(requestId, results);
    }

    @Override
    public MapCodec<? extends WSPacket> codec() {
        return CODEC;
    }
}
