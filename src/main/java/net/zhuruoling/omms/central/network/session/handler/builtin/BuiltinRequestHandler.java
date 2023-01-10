package net.zhuruoling.omms.central.network.session.handler.builtin;

import net.zhuruoling.omms.central.network.session.handler.RequestHandler;

public abstract class BuiltinRequestHandler extends RequestHandler {
    public BuiltinRequestHandler(){
        super("BUILTIN");
    }
}
