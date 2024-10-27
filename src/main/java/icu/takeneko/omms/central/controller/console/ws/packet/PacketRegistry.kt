package icu.takeneko.omms.central.controller.console.ws.packet

import com.mojang.serialization.MapCodec
import icu.takeneko.omms.central.foundation.registry.Identifier
import icu.takeneko.omms.central.foundation.registry.MapRegistry

@Suppress("UNCHECKED_CAST")
object PacketRegistry : MapRegistry<Identifier, MapCodec<WSPacket>>() {

    init {
        register(
            Identifier.of("connect"),
            WSConnectPacket.CODEC as MapCodec<WSPacket>
        )
        register(
            Identifier.of("ack"),
            WSAckPacket.CODEC as MapCodec<WSPacket>
        )
        register(
            Identifier.of("command"),
            WSCommandPacket.CODEC as MapCodec<WSPacket>
        )
        register(
            Identifier.of("log"),
            WSLogPacket.CODEC as MapCodec<WSPacket>
        )
        register(
            Identifier.of("disconnect"),
            WSDisconnectPacket.CODEC as MapCodec<WSPacket>
        )
        register(
            Identifier.of("completion_request"),
            WSCompletionRequestPacket.CODEC as MapCodec<WSPacket>
        )
        register(
            Identifier.of("completion_result"),
            WSCompletionResultPacket.CODEC as MapCodec<WSPacket>
        )
    }

    override fun register(key: Identifier, value: MapCodec<WSPacket>) {
        if (this.get(key) != null) {
            throw IllegalArgumentException("Duplicate packetType: $key")
        }
        super.register(key, value)
    }

}