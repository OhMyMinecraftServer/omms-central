package icu.takeneko.omms.central.network.session.handler.builtin.system;

import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Result;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.system.info.SystemInfo;
import icu.takeneko.omms.central.system.info.SystemInfoUtil;
import icu.takeneko.omms.central.util.Util;

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