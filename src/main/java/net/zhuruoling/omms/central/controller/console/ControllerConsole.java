package net.zhuruoling.omms.central.controller.console;

import kotlin.Unit;
import net.zhuruoling.omms.central.controller.Controller;
import net.zhuruoling.omms.central.util.io.PrintTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ControllerConsole extends Thread {
    private List<String> consoleBuffer = new ArrayList<>();
    private final Controller controller;
    private final ControllerWebSocketSession session;

    private final PrintTarget<?> printTarget;

    private final Logger logger = LoggerFactory.getLogger("ControllerConsole");

    public ControllerConsole(Controller controller, PrintTarget<?> printTarget) {
        super("ControllerConsole");
        this.controller = controller;
        this.printTarget = printTarget;
        session = new ControllerWebSocketSession(((that, s) -> {
            if (s.equals("\u1145:END:\u1919")) {
                that.close();
                this.interrupt();
                return Unit.INSTANCE;
            }
            this.printTarget.println(s);
            return Unit.INSTANCE;
        }), controller);
    }

    private void info(String info) {
        System.out.println(info);
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
