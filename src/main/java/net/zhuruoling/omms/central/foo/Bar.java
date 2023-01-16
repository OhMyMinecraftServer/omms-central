package net.zhuruoling.omms.central.foo;


import net.zhuruoling.omms.central.network.session.handler.builtin.UtilKt;
import net.zhuruoling.omms.central.network.session.request.RequestManager;

public class Bar {
    public static void main(String[] args) {
        UtilKt.registerBuiltinRequestHandlers();
        RequestManager.INSTANCE.getAllRegisteredRequest().forEach((s, requestHandler) -> {
            System.out.printf("\"%s\" to %s(),\n", s, requestHandler.getClass().getName());
        });
    }

}
