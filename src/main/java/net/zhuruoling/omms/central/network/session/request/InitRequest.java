package net.zhuruoling.omms.central.network.session.request;

public class InitRequest extends Request{
    long version = VERSION_BASE + 0xffffL;

    public static final long VERSION_BASE = 0xc0000000L;
    public InitRequest(long version) {
        super("PING");
        this.version = version;
    }

    public InitRequest(Request request, long version){
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
    public String toString() {
        return "InitRequest{" +
                "version=" + version +
                ", request='" + request + '\'' +
                ", content=" + content +
                '}';
    }
}
