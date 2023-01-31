package net.zhuruoling.omms.central.foo;


import net.zhuruoling.omms.central.network.session.handler.builtin.UtilKt;
import net.zhuruoling.omms.central.network.session.request.RequestManager;
import net.zhuruoling.omms.central.system.SystemUtil;

import java.util.Comparator;

public class Bar {
    public static void main(String[] args) {
        var info = SystemUtil.getStorageInfo();
        info.getStorageList().forEach(storage -> {
            System.out.printf("%s %f %f\n", storage.model(), storage.size()/1000.0/1000.0/1000.0, storage.size()/1024.0/1024.0/1024.0);
        });
        var mem = SystemUtil.getMemoryInfo();
        System.out.println(mem.getMemoryTotal()/1024.0/1024.0/1024.0);
    }

}
