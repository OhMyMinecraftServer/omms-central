package net.zhuruoling.foo;

import com.google.gson.GsonBuilder;
import net.zhuruoling.system.SystemInfo;
import net.zhuruoling.system.SystemUtil;

public class Rua {
    public static void main(String[] args) {
        //SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        SystemInfo info = new SystemInfo(SystemUtil.getSystemName(),SystemUtil.getSystemVersion(), SystemUtil.getSystemArch(), SystemUtil.getFileSystemInfo(), SystemUtil.getMemoryInfo(), SystemUtil.getNetworkInfo(), SystemUtil.getProcessorInfo(), SystemUtil.getStorageInfo());
        System.out.println(new GsonBuilder().serializeNulls().create().toJson(info));
    }
}
