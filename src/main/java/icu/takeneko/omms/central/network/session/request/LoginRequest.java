package icu.takeneko.omms.central.network.session.request;

import org.jetbrains.annotations.NotNull;

public class LoginRequest extends Request {
    long version = VERSION_BASE + 0xffffL;

    public static final long VERSION_BASE = 0xc0000000L;

    public LoginRequest(long version) {
        super("PING");
        this.version = version;
    }

    public LoginRequest(@NotNull Request request, long version) {
        super();
        this.request = request.getRequest();
        this.content = request.content;
        this.version = version;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public @NotNull String toString() {
        return "InitRequest{" +
                "version=" + version +
                ", request='" + request + '\'' +
                ", content=" + content +
                '}';
    }
}
