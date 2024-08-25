package icu.takeneko.omms.central.main;

import icu.takeneko.omms.central.RunConfiguration;
import icu.takeneko.omms.central.SharedObjects;
import icu.takeneko.omms.central.State;
import icu.takeneko.omms.central.config.Config;
import icu.takeneko.omms.central.fundation.FeatureOption;
import icu.takeneko.omms.central.graphics.GuiMainKt;
import icu.takeneko.omms.central.network.ChatbridgeImplementation;
import org.jetbrains.annotations.NotNull;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        FeatureOption.INSTANCE.parse(args);
        RunConfiguration.INSTANCE.getArgs().addAll(Arrays.stream(args).toList());
        var env = System.getProperties();
        if (env.containsKey("omms.consoleFont")) {
            RunConfiguration.INSTANCE.setConsoleFont(env.getProperty("omms.consoleFont"));
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!State.INSTANCE.getNormalShutdown() && CentralServer.INSTANCE.getInitialized()) {
                System.out.println("Stopping!");
                SharedObjects.INSTANCE.getHttpServer().interrupt();
                if (Objects.requireNonNull(Config.INSTANCE.getConfig()).getChatbridgeImplementation() == ChatbridgeImplementation.UDP) {
                    SharedObjects.INSTANCE.getUdpBroadcastReceiver().interrupt();
                    SharedObjects.INSTANCE.getUdpBroadcastSender().setStopped(true);
                }
                Objects.requireNonNull(SharedObjects.INSTANCE.getSocketServer()).interrupt();
                System.out.println("Bye");
            }
        }, "ShutdownHook"));
        System.out.println("Starting icu.takeneko.omms.central.main.MainKt");
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        new Thread("Server Thread") {
            @Override
            public void run() {
                CentralServer.INSTANCE.main(args);
            }
        }.start();
        if (FeatureOption.INSTANCE.get("gui")){
            GuiMainKt.guiMain();
        }
    }
}
