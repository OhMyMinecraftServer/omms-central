package icu.takeneko.omms.central.main;

import icu.takeneko.omms.central.network.ChatbridgeImplementation;
import icu.takeneko.omms.central.GlobalVariable;
import icu.takeneko.omms.central.config.Config;
import icu.takeneko.omms.central.network.ChatbridgeImplementation;
import icu.takeneko.omms.central.util.Util;
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
        var env = System.getenv();
        if (env.containsKey("omms.consoleFont")){
            GlobalVariable.INSTANCE.setConsoleFont(env.get("omms.consoleFont"));
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!GlobalVariable.INSTANCE.getNormalShutdown() && CentralServer.INSTANCE.getInitialized()) {
                System.out.println("Stopping!");
                Objects.requireNonNull(GlobalVariable.INSTANCE.getHttpServer()).interrupt();
                if (Objects.requireNonNull(Config.INSTANCE.getConfig()).getChatbridgeImplementation() == ChatbridgeImplementation.UDP) {
                    Objects.requireNonNull(GlobalVariable.INSTANCE.getReceiver()).interrupt();
                    Objects.requireNonNull(GlobalVariable.INSTANCE.getUdpBroadcastSender()).setStopped(true);
                }
                Objects.requireNonNull(GlobalVariable.INSTANCE.getSocketServer()).interrupt();
                System.out.println("Bye");
                //Runtime.getRuntime().halt(0);
            }
        }, "ShutdownHook"));
        System.out.println("Starting icu.takeneko.omms.central.main.MainKt");
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        CentralServer.main(args);
    }
}
