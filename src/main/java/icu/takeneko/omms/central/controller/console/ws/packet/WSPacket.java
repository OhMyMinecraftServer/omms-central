package icu.takeneko.omms.central.controller.console.ws.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;
import icu.takeneko.omms.central.fundation.registry.Identifier;
import icu.takeneko.omms.central.foundation.serialization.DispatchedCodec;

public interface WSPacket {
    @SuppressWarnings("unchecked")
    Codec<WSPacket> CODEC = new DispatchedCodec<>(
        PacketRegistry.INSTANCE,
        it -> (MapCodec<WSPacket>) it.codec(),
        ins -> PacketRegistry.INSTANCE.reversedLookup().get((MapCodec<WSPacket>) ins.codec()),
        Identifier.CODEC,
        "type"
    ).codec();

    void handle(WSPacketHandler handler);

    MapCodec<? extends WSPacket> codec();
}
