package icu.takeneko.omms.central.controller.console.ws.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;

public class WSDisconnectPacket extends WSPacket<WSDisconnectPacket>{

    public static final Codec<WSDisconnectPacket> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("unused").forGetter(o -> o.unused)
    ).apply(ins, WSDisconnectPacket::new));
    private final int unused = 0;

    WSDisconnectPacket(int unused){
        this();
    }

    public WSDisconnectPacket() {
        super(PacketTypes.DISCONNECT);
    }

    @Override
    public void handle(WSPacketHandler handler) {
    }
}
