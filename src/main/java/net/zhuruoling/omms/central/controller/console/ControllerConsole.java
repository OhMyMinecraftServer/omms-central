package net.zhuruoling.omms.central.controller.console;

import kotlin.Unit;
import net.zhuruoling.omms.central.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ControllerConsole extends Thread {
    private List<String> consoleBuffer = new ArrayList<>();
    private final Controller controller;
    private final ControllerWebSocketSession session;
    private final boolean useLogger;
    private final Logger logger = LoggerFactory.getLogger("ControllerConsole");

    public ControllerConsole(Controller controller) {
        this(controller, true);
    }

    public ControllerConsole(Controller controller, boolean useLogger) {
        super("ControllerConsole");
        this.controller = controller;
        this.useLogger = useLogger;
        session = new ControllerWebSocketSession(((that, s) -> {
            if (s.equals("\u1145:END:\u1919")) {
                that.close();
                this.interrupt();
                return Unit.INSTANCE;
            }
            System.out.println(s);
            return Unit.INSTANCE;
        }), controller);
    }

    private void info(String info) {
        if (useLogger) {
            logger.info(info);
        }else {
            System.out.println(info);
        }
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
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
        while (scanner.hasNext()) {
            var line = scanner.nextLine();
            if (line.startsWith(":")) {
                if (line.equals(":q")) {
                    info("Disconnecting.");
                    session.close();
                    return;
                }
            } else {
                session.inputLine(line);
            }
        }
    }
}
