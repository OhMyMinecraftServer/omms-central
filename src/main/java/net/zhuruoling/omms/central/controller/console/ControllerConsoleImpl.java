package net.zhuruoling.omms.central.controller.console;

import kotlin.Unit;
import net.zhuruoling.omms.central.controller.Controller;
import net.zhuruoling.omms.central.controller.ControllerImpl;
import net.zhuruoling.omms.central.controller.console.output.PrintTarget;
import net.zhuruoling.omms.central.controller.console.input.InputSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerConsoleImpl extends Thread implements ControllerConsole{
    private final Controller controller;

    public Controller getController() {
        return controller;
    }


    public static @NotNull ControllerConsole newInstance(Controller controller, String consoleId, PrintTarget<?, ControllerConsole> printTarget, InputSource inputSource) {
        return new ControllerConsoleImpl(controller, consoleId, printTarget, inputSource);
    }

    private final @NotNull ControllerWebSocketSession session;

    private final PrintTarget<?, ControllerConsole> printTarget;
    private final InputSource inputSource;

    private final String consoleId;

    private final Logger logger = LoggerFactory.getLogger("ControllerConsole");

    private ControllerConsoleImpl(Controller controller, String consoleId, PrintTarget<?, ControllerConsole> printTarget, InputSource inputSource) {
        super("ControllerConsole");
        this.controller = controller;
        this.printTarget = printTarget;
        this.inputSource = inputSource;
        this.consoleId = consoleId;
        session = new ControllerWebSocketSession((that, s) -> {
            if (s.equals("\u1145:END:\u1919")) {
                that.close();
                this.interrupt();
                return Unit.INSTANCE;
            }
            this.printTarget.println(s, this);
            return Unit.INSTANCE;
        }, (ControllerImpl) this.controller);
    }

    private void info(String info) {
        printTarget.println(info, this);
    }

    public void close() {
        session.close();
        this.interrupt();
    }

    public void input(@Nullable String line) {
        if (line == null)return;
        if(line.isEmpty())return;
        if (line.startsWith(":")) {
            if (line.equals(":q")) {
                info("Disconnecting.");
                session.close();
                this.interrupt();
            }
        } else {
            session.inputLine(line);
        }
    }



    @Override
    public void run() {
        session.start();
        while (!session.getConnected().get()) {
            try {
                Thread.sleep(10);
                if (!session.isAlive()) return;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        info("Connected.");
        var line = inputSource.getLine();
        while (true) {
            try {
                input(line);
                sleep(10);
                line = inputSource.getLine();
            } catch (InterruptedException ignored) {
                break;
            }
        }
    }

    public String getConsoleId() {
        return consoleId;
    }

    public InputSource getInputSource() {
        return inputSource;
    }
}
