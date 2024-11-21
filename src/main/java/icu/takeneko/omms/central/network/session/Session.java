package icu.takeneko.omms.central.network.session;


import icu.takeneko.omms.central.config.Config;
import io.ktor.network.sockets.Socket;
import io.ktor.utils.io.ByteReadChannel;
import io.ktor.utils.io.ByteWriteChannel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Session {
    private final Socket socket;
    @Setter
    private byte[] key;

    private final ByteReadChannel readChannel;
    private final ByteWriteChannel writeChannel;

    public Session(Socket socket, byte[] key, ByteReadChannel readChannel, ByteWriteChannel writeChannel) {
        this.socket = socket;
        this.key = key;
        this.readChannel = readChannel;
        this.writeChannel = writeChannel;
    }

    public EncryptedMessageChannel createChannel(){
        return new EncryptedMessageChannel(readChannel, writeChannel, Config.config.getRateLimit(), key);
    }
}
