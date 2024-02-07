package icu.takeneko.omms.central.network.session.handler.builtin;

import icu.takeneko.omms.central.network.session.handler.RequestHandler;

public abstract class BuiltinRequestHandler extends RequestHandler {
    public BuiltinRequestHandler(){
        super("BUILTIN");
    }
}
