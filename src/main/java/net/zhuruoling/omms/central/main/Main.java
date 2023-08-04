package net.zhuruoling.omms.central.main;

import net.zhuruoling.omms.central.GlobalVariable;
import net.zhuruoling.omms.central.network.ChatbridgeImplementation;
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
        GlobalVariable.INSTANCE.setArgs(args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!GlobalVariable.INSTANCE.getNormalShutdown() && CentralServer.INSTANCE.getInitialized()) {
                System.out.println("Stopping!");
                Objects.requireNonNull(GlobalVariable.INSTANCE.getHttpServer()).interrupt();
                if (Objects.requireNonNull(GlobalVariable.INSTANCE.getConfig()).getChatbridgeImplementation() == ChatbridgeImplementation.UDP) {
                    Objects.requireNonNull(GlobalVariable.INSTANCE.getReceiver()).interrupt();
                    Objects.requireNonNull(GlobalVariable.INSTANCE.getUdpBroadcastSender()).setStopped(true);
                }
                Objects.requireNonNull(GlobalVariable.INSTANCE.getSocketServer()).interrupt();
                System.out.println("Bye");
                //Runtime.getRuntime().halt(0);
            }
        }, "ShutdownHook"));
        System.out.println("Starting net.zhuruoling.omms.central.main.MainKt");
        //SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        CentralServer.main(args);
    }
}
