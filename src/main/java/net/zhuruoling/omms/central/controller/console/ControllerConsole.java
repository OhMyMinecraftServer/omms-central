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
    private final Logger logger = LoggerFactory.getLogger("ControllerConsole");

    public ControllerConsole(Controller controller) {
        super("ControllerConsole");
        this.controller = controller;
        session = new ControllerWebSocketSession(((that, s) -> {
            if (s.equals("\u1145:END:\u1919")){
                that.close();
                this.interrupt();
                return Unit.INSTANCE;
            }
            System.out.println(s);
            return Unit.INSTANCE;
        }), controller);
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
        logger.info("Connected.");
        while (scanner.hasNext()) {
            var line = scanner.nextLine();
            if (line.startsWith(":")) {
                if (line.equals(":q")) {
                    logger.info("Disconnecting.");
                    session.close();
                    return;
                }
            } else {
                session.inputLine(line);
            }
        }
    }
}
