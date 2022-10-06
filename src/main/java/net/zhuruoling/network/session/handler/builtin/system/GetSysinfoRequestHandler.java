package net.zhuruoling.network.session.handler.builtin.system;

import net.zhuruoling.network.session.HandlerSession;
import net.zhuruoling.network.session.handler.builtin.BuiltinRequestHandler;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.system.SystemInfo;
import net.zhuruoling.system.SystemUtil;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;

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
