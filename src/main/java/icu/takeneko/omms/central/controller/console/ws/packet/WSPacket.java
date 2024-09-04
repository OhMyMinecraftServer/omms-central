package icu.takeneko.omms.central.controller.console.ws.packet;

import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;
import lombok.Getter;

@Getter
public abstract class WSPacket<T extends WSPacket<T>> {
    private final PacketType<T> packetType;

    protected WSPacket(PacketType<T> packetType) {
        this.packetType = packetType;
    }

    public String encodeSelf() {
        return packetType.encode((T) this);
    }

    abstract public void handle(WSPacketHandler handler);

    public static <T extends WSPacket<T>> WSPacket<? extends T> cast(T packet) {
        return packet;
    }
}
