package net.zhuruoling.omms.central.main;

import net.zhuruoling.omms.central.GlobalVariable;
import net.zhuruoling.omms.central.network.ChatbridgeImplementation;
import net.zhuruoling.omms.central.plugin.PluginManager;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class Main {
    public static void main(String @NotNull [] args) throws IOException {
        if (Arrays.stream(args).toList().contains("--controllerConsole")){
            RemoteControllerConsoleMain.main(args);
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!GlobalVariable.INSTANCE.getNormalShutdown() && MainKt.INSTANCE.getInitialized()) {
                    System.out.println("Stopping!");
                    PluginManager.INSTANCE.unloadAll();
                    Objects.requireNonNull(GlobalVariable.INSTANCE.getHttpServer()).interrupt();
                    if (Objects.requireNonNull(GlobalVariable.INSTANCE.getConfig()).getChatbridgeImplementation() == ChatbridgeImplementation.UDP) {
                        Objects.requireNonNull(GlobalVariable.INSTANCE.getReceiver()).interrupt();
                        Objects.requireNonNull(GlobalVariable.INSTANCE.getUdpBroadcastSender()).setStopped(true);
                    }
                    Objects.requireNonNull(GlobalVariable.INSTANCE.getSocketServer()).interrupt();
                    if (!GlobalVariable.INSTANCE.getNoLock()) {
                        System.out.println("Releasing lock.");
                        Util.releaseLock(Objects.requireNonNull(GlobalVariable.INSTANCE.getLock()));
                        Files.delete(Path.of(Util.LOCK_NAME));
                    }
                    System.out.println("Bye");
                    //Runtime.getRuntime().halt(0);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "ShutdownHook"));
        System.out.println("Starting net.zhuruoling.omms.central.main.MainKt");
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        MainKt.main(args);
    }
}
