package icu.takeneko.omms.central.controller.console;

import icu.takeneko.omms.central.controller.Controller;
import icu.takeneko.omms.central.controller.ControllerImpl;
import icu.takeneko.omms.central.controller.console.input.InputSource;
import icu.takeneko.omms.central.controller.console.output.PrintTarget;
import icu.takeneko.omms.central.controller.console.ws.ControllerWebSocketSession;
import icu.takeneko.omms.central.controller.console.ws.PacketType;
import icu.takeneko.omms.central.controller.console.ws.WSPacketHandler;
import icu.takeneko.omms.central.controller.console.ws.WSStatusPacket;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ControllerConsoleImpl extends Thread implements ControllerConsole {
    public static final WSStatusPacket DISCONNECT_PACKET = new WSStatusPacket(PacketType.DISCONNECT);

    private final Controller controller;
    private final @NotNull ControllerWebSocketSession session;
    private final PrintTarget<?, ControllerConsole> printTarget;
    private final InputSource inputSource;
    private final String consoleId;
    private final Logger logger = LoggerFactory.getLogger("ControllerConsole");
    private final CompletableFuture<Boolean> gracefullyStopped = new CompletableFuture<>();

    public Controller getController() {
        return controller;
    }

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
        });
    }

    private void info(String info) {
        printTarget.println(info, this);
    }

    public boolean gracefullyStop() {
        try {
            session.packet(DISCONNECT_PACKET);
            gracefullyStopped.get(5000, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            logger.warn("Console Session was not gracefully terminated as the disconnect request did not complete properly: {}", e.toString());
            return false;
        }
    }

    public void close() {
        session.close();
        this.interrupt();
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
            var line = inputSource.getLine();
            while (true) {
                input(line);
                LockSupport.parkNanos(1000);
                line = inputSource.getLine();
            }
        }catch (Exception ignored){
        }
    }

    public String getConsoleId() {
        return consoleId;
    }

    public InputSource getInputSource() {
        return inputSource;
    }
}
