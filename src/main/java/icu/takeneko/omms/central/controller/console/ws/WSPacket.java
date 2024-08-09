package icu.takeneko.omms.central.controller.console.ws;

public interface WSPacket {
    void handle(WSPacketHandler handler);

    public static <T extends WSPacket> WSPacket cast(T packet){
        return (WSPacket) packet;
    }
}
