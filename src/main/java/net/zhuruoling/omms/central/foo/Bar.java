package net.zhuruoling.omms.central.foo;


import net.zhuruoling.omms.central.network.session.handler.builtin.UtilKt;
import net.zhuruoling.omms.central.network.session.request.RequestManager;

import java.util.Comparator;

public class Bar {
    public static void main(String[] args) {
        UtilKt.registerBuiltinRequestHandlers();
//        RequestManager.INSTANCE.getAllRegisteredRequest().entrySet().stream().toList().sort(entry ->);
        RequestManager.INSTANCE.getAllRegisteredRequest().forEach((s, requestHandler) -> {
            System.out.printf("\"%s\" to %s(),\n", s, requestHandler.getClass().getName());
        });
    }

}
