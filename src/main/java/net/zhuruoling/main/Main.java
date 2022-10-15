package net.zhuruoling.main;

import net.zhuruoling.plugin.PluginManager;
import net.zhuruoling.util.Util;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        System.out.println("Starting net.zhuruoling.main.MainKt");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!RuntimeConstants.INSTANCE.getNormalShutdown()) {
                    System.out.println("Stopping!");
                    PluginManager.INSTANCE.unloadAll();
                    Objects.requireNonNull(RuntimeConstants.INSTANCE.getHttpServer()).interrupt();
                    Objects.requireNonNull(RuntimeConstants.INSTANCE.getReceiver()).interrupt();
                    Objects.requireNonNull(RuntimeConstants.INSTANCE.getUdpBroadcastSender()).setStopped(true);
                    Objects.requireNonNull(RuntimeConstants.INSTANCE.getSocketServer()).interrupt();
                    if (!RuntimeConstants.INSTANCE.getNoLock()) {
                        System.out.println("Releasing lock.");
                        Util.releaseLock(RuntimeConstants.INSTANCE.getLock());
                        Files.delete(Path.of(Util.LOCK_NAME));
                    }
                    System.out.println("Bye");
                    //Runtime.getRuntime().halt(0);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "ShutdownHook"));
        MainKt.main(args);
    }
}
