package icu.takeneko.omms.central.controller.console.ws.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;
import icu.takeneko.omms.central.util.Util;
import lombok.Getter;

public class WSCompletionRequestPacket extends WSPacket<WSCompletionRequestPacket>{


    public static final Codec<WSCompletionRequestPacket> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.STRING.fieldOf("requestId").forGetter(o -> o.requestId),
            Codec.STRING.fieldOf("input").forGetter(o -> o.input),
            Codec.INT.fieldOf("cursorPosition").forGetter(o -> o.cursorPosition)
    ).apply(ins, WSCompletionRequestPacket::new));

    @Getter
    private final String requestId;
    private final String input;
    private final int cursorPosition;

    public WSCompletionRequestPacket(String requestId, String input, int cursorPosition) {
        super(PacketTypes.COMPLETION_REQUEST);
        this.requestId = requestId;
        this.input = input;
        this.cursorPosition = cursorPosition;
    }

    public WSCompletionRequestPacket(String input, int cursorPosition) {
        super(PacketTypes.COMPLETION_REQUEST);
        this.requestId = Util.generateRandomString(8);
        this.input = input;
        this.cursorPosition = cursorPosition;
    }

    @Override
    public void handle(WSPacketHandler handler) {

    }
}