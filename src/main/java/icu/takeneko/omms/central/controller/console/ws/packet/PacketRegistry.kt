package icu.takeneko.omms.central.controller.console.ws.packet

import icu.takeneko.omms.central.fundation.registry.Identifier
import icu.takeneko.omms.central.fundation.registry.MapRegistry
import io.ktor.util.*

object PacketRegistry : MapRegistry<Identifier, PacketType<*>>() {
    override fun register(key: Identifier, value: PacketType<*>) {
        if (this.get(key) != null) {
            throw IllegalArgumentException("Duplicate packetType: $key")
        }
        super.register(key, value)
    }

    fun encodePacket(packet: WSPacket<*>): String {
        val pt = packet.packetType
        val packetContent = packet.encodeSelf().encodeBase64()
        val registryKey = getKey(pt) ?: throw IllegalArgumentException("")
        return "${registryKey.toString().encodeBase64()}::$packetContent"
    }

    fun decodePacket(content: String): WSPacket<*> {
        val (key, line) = content.split("::")
        val packetType = get(Identifier(key.decodeBase64String()))
            ?: throw IllegalArgumentException("Unknown packet type: ${key.decodeBase64String()}")
        val packetContent = line.decodeBase64String()
        return packetType.decode(packetContent)
    }

}