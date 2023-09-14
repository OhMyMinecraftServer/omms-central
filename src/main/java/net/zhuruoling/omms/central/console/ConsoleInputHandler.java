package net.zhuruoling.omms.central.console;

import net.zhuruoling.omms.central.GlobalVariable;
import net.zhuruoling.omms.central.command.CommandManager;
import net.zhuruoling.omms.central.command.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class ConsoleInputHandler {
    private Terminal terminal;

    public void prepareTerminal(){
        try {
            terminal = TerminalBuilder.builder().system(true).dumb(true).build();
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
                    .history(GlobalVariable.INSTANCE.getConsoleHistory())
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
