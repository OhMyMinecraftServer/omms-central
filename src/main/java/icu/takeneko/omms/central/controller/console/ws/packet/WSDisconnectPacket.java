package icu.takeneko.omms.central.controller.console.ws.packet;

import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;

public class WSDisconnectPacket extends WSPacket<WSDisconnectPacket>{


    protected WSDisconnectPacket() {
        super(packetType);
    }

    @Override
    public void handle(WSPacketHandler handler) {

    }
}
