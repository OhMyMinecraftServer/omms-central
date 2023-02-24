package net.zhuruoling.omms.central.controller.console;

import kotlin.Unit;
import net.zhuruoling.omms.central.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ControllerConsole {
    private List<String> consoleBuffer = new ArrayList<>();
    private final Controller controller;
    private final ControllerWebSocketSession session;
    private final Logger logger = LoggerFactory.getLogger("ControllerConsole");

    public ControllerConsole(Controller controller) {
        this.controller = controller;
        session = new ControllerWebSocketSession((s -> {
            logger.info(s);
            return Unit.INSTANCE;
        }), controller);
    }

    public void start() {
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
        logger.info("Connected.");
        while (scanner.hasNext()) {
            var line = scanner.nextLine();
            if (line.startsWith(":")) {
                if (line.equals(":q")) break;
            } else {
                session.inputLine(line);
            }
        }
    }
}
