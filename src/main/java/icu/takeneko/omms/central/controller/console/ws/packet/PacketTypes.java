package icu.takeneko.omms.central.controller.console.ws.packet;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import icu.takeneko.omms.central.fundation.registry.Identifier;

public class PacketTypes {
    public static final PacketType<WSConnectPacket> CONNECT = register(
            Identifier.of("connect"),
            WSConnectPacket.class,
            WSConnectPacket.CODEC
    );

    public static final PacketType<WSAckPacket> ACK = register(
            Identifier.of("ack"),
            WSAckPacket.class,
            WSAckPacket.CODEC
    );
    public static final PacketType<WSCommandPacket> COMMAND = register(
            Identifier.of("command"),
            WSCommandPacket.class,
            WSCommandPacket.CODEC
    );
    public static final PacketType<WSLogPacket> LOG = register(
            Identifier.of("log"),
            WSLogPacket.class,
            WSLogPacket.CODEC
    );

    public static final PacketType<WSDisconnectPacket> DISCONNECT = register(
            Identifier.of("disconnect"),
            WSDisconnectPacket.class,
            WSDisconnectPacket.CODEC
    );


    private static <T extends WSPacket<T>> PacketType<T> register(Identifier id, Class<? extends T> clazz, Codec<T> codec) {
        PacketType<T> pc = new PacketType<>(
                clazz,
                s -> codec.decode(JsonOps.INSTANCE, JsonParser.parseString(s))
                        .getOrThrow(false, s1 -> {
                        })
                        .getFirst(),
                it -> codec.encodeStart(JsonOps.INSTANCE, it)
                        .getOrThrow(false, s1 -> {
                        })
                        .toString()

        );
        PacketRegistry.INSTANCE.register(id, pc);
        return pc;
    }
}
