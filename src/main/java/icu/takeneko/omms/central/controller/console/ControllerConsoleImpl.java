package icu.takeneko.omms.central.controller.console;

import icu.takeneko.omms.central.controller.Controller;
import icu.takeneko.omms.central.controller.ControllerImpl;
import icu.takeneko.omms.central.controller.console.input.InputSource;
import icu.takeneko.omms.central.controller.console.output.PrintTarget;
import icu.takeneko.omms.central.controller.console.ws.ControllerWebSocketSession;
import icu.takeneko.omms.central.controller.console.ws.packet.PacketType;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;
import icu.takeneko.omms.central.controller.console.ws.packet.WSDisconnectPacket;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ControllerConsoleImpl extends Thread implements ControllerConsole {
    @Getter
    private final Controller controller;
    private final @NotNull ControllerWebSocketSession session;
    private final PrintTarget<?, ControllerConsole> printTarget;
    @Getter
    private final InputSource inputSource;
    @Getter
    private final String consoleId;
    private final Logger logger = LoggerFactory.getLogger("ControllerConsole");
    private final CompletableFuture<Boolean> gracefullyStopped = new CompletableFuture<>();

    public static @NotNull ControllerConsole newInstance(Controller controller, String consoleId, PrintTarget<?, ControllerConsole> printTarget, InputSource inputSource) {
        return new ControllerConsoleImpl(controller, consoleId, printTarget, inputSource);
    }


    private ControllerConsoleImpl(Controller controller, String consoleId, PrintTarget<?, ControllerConsole> printTarget, InputSource inputSource) {
        super("ControllerConsole");
        this.controller = controller;
        this.printTarget = printTarget;
        this.inputSource = inputSource;
        this.consoleId = consoleId;
        session = new ControllerWebSocketSession((ControllerImpl) this.controller, new WSPacketHandler() {
            @Override
            public void onConnect(int version) {
            }

            @Override
            public void onDisconnect() {
                gracefullyStopped.complete(true);
            }

            @Override
            public void onLog(List<String> line) {
                printTarget.printMultiLine(line, ControllerConsoleImpl.this);
            }

            @Override
            public void onCompletionResult(String requestId, List<String> result) {
                session.handleCompletionResult(requestId, result);
            }
        });
    }

    private void info(String info) {
        printTarget.println(info, this);
    }

    public boolean gracefullyStop() {
        try {
            session.packet(new WSDisconnectPacket());
            gracefullyStopped.get(5000, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            logger.warn("Console Session was not gracefully terminated as the disconnect request did not complete properly: {}", e.toString());
            return false;
        }
    }

    public void close() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        session.close();
        this.interrupt();
    }

    @Override
    public CompletableFuture<List<String>> complete(String input, int cursorPos) {
        return session.requestCompletion(input, cursorPos);
    }

    public void input(@Nullable String line) {
        if (line == null) return;
        if (line.isEmpty()) return;
        if (line.startsWith(":")) {
            if (line.equals(":q")) {
                info("Disconnecting.");
                if (!gracefullyStop()){
                    close();
                } else {
                    interrupt();
                }
            }
            if (line.startsWith(":c ")){
                String partialCommand = line.replaceFirst(":c ", "");
                this.complete(partialCommand, partialCommand.length())
                        .thenAccept(result -> result.forEach(logger::info));
            }
        } else {
            session.command(line);
        }
    }


    @Override
    public void run() {
        try {
            session.start();
            while (!session.getConnected().get()) {
                LockSupport.parkNanos(1000);
                if (!session.isAlive()) return;
            }
            info("Connected.");
            SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J();
            var line = inputSource.getLine();
            while (true) {
                input(line);
                LockSupport.parkNanos(1000);
                line = inputSource.getLine();
            }
        }catch (Exception ignored){
        }
    }

}
