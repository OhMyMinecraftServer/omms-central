package net.zhuruoling.omms.central.network.session.handler.builtin.system;

import net.zhuruoling.omms.central.network.session.SessionContext;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.system.info.SystemInfo;
import net.zhuruoling.omms.central.system.info.SystemInfoUtil;
import net.zhuruoling.omms.central.util.Util;

public class GetSysinfoRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(Request request, SessionContext session) {
        SystemInfo info = new SystemInfo(SystemInfoUtil.getSystemName(), SystemInfoUtil.getSystemVersion(), SystemInfoUtil.getSystemArch(), SystemInfoUtil.getFileSystemInfo(), SystemInfoUtil.getMemoryInfo(), SystemInfoUtil.getNetworkInfo(), SystemInfoUtil.getProcessorInfo(), SystemInfoUtil.getStorageInfo());
        return new Response().withResponseCode(Result.SYSINFO_GOT).withContentPair("systemInfo", Util.gson.toJson(info, SystemInfo.class));
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}