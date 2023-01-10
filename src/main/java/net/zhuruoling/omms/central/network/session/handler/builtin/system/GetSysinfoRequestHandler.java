package net.zhuruoling.omms.central.network.session.handler.builtin.system;

import net.zhuruoling.omms.central.network.session.HandlerSession;
import net.zhuruoling.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.omms.central.network.session.request.Request;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.system.SystemInfo;
import net.zhuruoling.omms.central.system.SystemUtil;
import net.zhuruoling.omms.central.util.Result;
import net.zhuruoling.omms.central.util.Util;

public class GetSysinfoRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(Request request, HandlerSession session) {
        SystemInfo info = new SystemInfo(SystemUtil.getSystemName(),SystemUtil.getSystemVersion(), SystemUtil.getSystemArch(),SystemUtil.getFileSystemInfo(), SystemUtil.getMemoryInfo(), SystemUtil.getNetworkInfo(), SystemUtil.getProcessorInfo(), SystemUtil.getStorageInfo());
        return new Response().withResponseCode(Result.OK).withContentPair("systemInfo", Util.gson.toJson(info, SystemInfo.class));
    }

    @Override
    public Permission requiresPermission() {
        return Permission.SERVER_OS_CONTROL;
    }
}
