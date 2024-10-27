package icu.takeneko.omms.central.controller.console.ws.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;
import icu.takeneko.omms.central.foundation.serialization.EnumCodec;
import icu.takeneko.omms.central.foundation.serialization.SerializableEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WSAckPacket implements WSPacket {
    public static final MapCodec<WSAckPacket> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.INT.optionalFieldOf("clientVersion").forGetter(o ->
                    o.clientVersion <= 0 ? Optional.empty() : Optional.of(o.clientVersion)
            ),
            Action.CODEC.fieldOf("action").forGetter(o -> o.action)
    ).apply(ins, WSAckPacket::new));

    private final int clientVersion;
    private final Action action;

    protected WSAckPacket(Optional<Integer> clientVersion, Action action) {
        this.clientVersion = clientVersion.orElse(-1);
        this.action = action;
    }

    @Override
    public void handle(WSPacketHandler handler) {
        switch (action) {
            case CONNECT -> handler.onConnect(clientVersion);
            case DISCONNECT -> handler.onDisconnect();
        }
    }

    @Override
    public MapCodec<? extends WSPacket> codec() {
        return CODEC;
    }

    public enum Action implements SerializableEnum {
        CONNECT, DISCONNECT;

        public static final Codec<Action> CODEC = EnumCodec.of(Action::valueOf);

        @Override

        @NotNull
        public String getSerializedName() {
            return name();
        }
    }
}
