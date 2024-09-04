package icu.takeneko.omms.central.controller.console.ws.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;

public class WSConnectPacket extends WSPacket<WSConnectPacket>{
    public static final Codec<WSConnectPacket> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("version").forGetter(o -> o.version)
    ).apply(ins, WSConnectPacket::new));

    private final int version;

    protected WSConnectPacket(int version) {
        super(PacketTypes.CONNECT);
        this.version = version;
    }

    @Override
    public void handle(WSPacketHandler handler) {

    }
}
