package net.zhuruoling.network.session.request;

import com.sun.jna.platform.win32.WinNT;

public class InitRequest extends Request{
    long version = VERSION_BASE + 0xffffL;

    public static final long VERSION_BASE = 0xc000_0000L;
    public InitRequest(String req, long version) {
        super(req);
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
}
