package icu.takeneko.omms.central.network.session.handler.builtin.system;

import icu.takeneko.omms.central.network.session.SessionContext;
import icu.takeneko.omms.central.network.session.handler.builtin.BuiltinRequestHandler;
import icu.takeneko.omms.central.network.session.request.Request;
import icu.takeneko.omms.central.network.session.response.Response;
import icu.takeneko.omms.central.network.session.response.Status;
import icu.takeneko.omms.central.permission.Permission;
import icu.takeneko.omms.central.system.info.SystemInfo;
import icu.takeneko.omms.central.system.info.SystemInfoUtil;
import icu.takeneko.omms.central.util.Util;

public class GetSysinfoRequestHandler extends BuiltinRequestHandler {

    @Override
    public Response handle(Request request, SessionContext session) {
        SystemInfo info = new SystemInfo(SystemInfoUtil.getSystemName(), SystemInfoUtil.getSystemVersion(), SystemInfoUtil.getSystemArch(), SystemInfoUtil.getFileSystemInfo(), SystemInfoUtil.getMemoryInfo(), SystemInfoUtil.getNetworkInfo(), SystemInfoUtil.getProcessorInfo(), SystemInfoUtil.getStorageInfo());
        return request.success()
            .withContentPair("systemInfo", info);
    }

    @Override
    public Permission requiresPermission() {
        return null;
    }
}