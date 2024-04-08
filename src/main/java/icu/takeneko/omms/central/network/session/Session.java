package icu.takeneko.omms.central.network.session;


import icu.takeneko.omms.central.config.Config;
import io.ktor.network.sockets.Socket;
import io.ktor.utils.io.ByteReadChannel;
import io.ktor.utils.io.ByteWriteChannel;

public class Session {
    private final Socket socket;
    private byte[] key;

    private final ByteReadChannel readChannel;
    private final ByteWriteChannel writeChannel;

    public Session(Socket socket, byte[] key, ByteReadChannel readChannel, ByteWriteChannel writeChannel) {
        this.socket = socket;
        this.key = key;
        this.readChannel = readChannel;
        this.writeChannel = writeChannel;
    }

    public Socket getSocket() {
        return socket;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public ByteReadChannel getReadChannel() {
        return readChannel;
    }

    public ByteWriteChannel getWriteChannel() {
        return writeChannel;
    }

    public EncryptedMessageChannel createChannel(){
        return new EncryptedMessageChannel(readChannel, writeChannel, Config.config.getRateLimit(), key);
    }
}
