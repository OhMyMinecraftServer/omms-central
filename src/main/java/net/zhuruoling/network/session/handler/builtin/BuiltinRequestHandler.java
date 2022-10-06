package net.zhuruoling.network.session.handler.builtin;

import net.zhuruoling.network.session.handler.RequestHandler;

public abstract class BuiltinRequestHandler extends RequestHandler {
    public BuiltinRequestHandler(){
        super("BUILTIN");
    }
}
