package net.zhuruoling.foo;


import net.zhuruoling.announcement.AnnouncementManager;
import net.zhuruoling.graphics.GraphicsUtilKt;
import net.zhuruoling.network.session.request.InitRequest;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.response.Response;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.util.Scanner;

public class Bar {
    public static void main(String[] args) {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        System.out.println(Util.toJson(new InitRequest(new Request("WDNMD").withContentKeyPair("a", "b"), InitRequest.VERSION_BASE + 0xffffL)));
        AnnouncementManager.INSTANCE.init();
        System.out.println(Util.toJson(new Response().withResponseCode(Result.OK).withContentPair("announcements", Util.toJson(AnnouncementManager.INSTANCE.getAnnouncementMap().keySet()))));
    }


}
