package icu.takeneko.omms.central.console;

import icu.takeneko.omms.central.SharedObjects;
import icu.takeneko.omms.central.command.CommandManager;
import icu.takeneko.omms.central.command.CommandSourceStack;
import icu.takeneko.omms.central.main.CentralServer;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class ConsoleInputHandler {
    private Terminal terminal;

    public void prepareTerminal() {
        try {
            terminal = TerminalBuilder.builder().system(true).dumb(true).signalHandler(new Terminal.SignalHandler() {
                @Override
                public void handle(Terminal.Signal signal) {
                    if (signal == Terminal.Signal.INT) {
                        CentralServer.INSTANCE.stop();
                    }
                }
            }).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ConsoleInputHandler() {

    }

    public static @NotNull ConsoleInputHandler INSTANCE = new ConsoleInputHandler();

    public void handle() {
        var completer = new BrigadierCommandCompleter();
        try {
            CommandManager.INSTANCE.reload();
            LineReader lineReader = LineReaderBuilder.builder()
                    .history(SharedObjects.INSTANCE.getConsoleHistory())
                    .terminal(terminal)
                    .completer(completer)
                    .build();
            String line = lineReader.readLine();
            line = line.strip().stripIndent().stripLeading().stripTrailing();
            if (line.isEmpty()) {
                return;
            }
            CommandManager.INSTANCE.dispatchCommand(line, new CommandSourceStack(CommandSourceStack.Source.CONSOLE));
        } catch (UserInterruptException | EndOfFileException ignored) {

        }
    }

    public Terminal getTerminal() {
        return terminal;
    }
}
