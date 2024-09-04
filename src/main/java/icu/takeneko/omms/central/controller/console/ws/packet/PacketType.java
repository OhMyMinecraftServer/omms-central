package icu.takeneko.omms.central.controller.console.ws.packet;

import java.util.function.Function;

public class PacketType<T extends WSPacket<T>> {
    private final Function<String, T> packetDecoder;
    private final Function<T, String> packetEncoder;
    private final Class<? extends WSPacket<T>> packetClass;

    public PacketType(Class<? extends WSPacket<T>> packetClass,
                      Function<String, T> packetDecoder,
                      Function<T, String> packetEncoder
    ) {
        this.packetDecoder = packetDecoder;
        this.packetEncoder = packetEncoder;
        this.packetClass = packetClass;
    }

    public String encode(T packetType){
        return packetEncoder.apply(packetType);
    }

    public T decode(String packet){
        return packetDecoder.apply(packet);
    }
}