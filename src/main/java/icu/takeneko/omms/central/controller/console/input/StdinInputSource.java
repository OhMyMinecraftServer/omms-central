package icu.takeneko.omms.central.controller.console.input;

import icu.takeneko.omms.central.console.ConsoleInputHandler;
import icu.takeneko.omms.central.controller.console.ControllerConsole;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;

public class StdinInputSource extends InputSource {

    private final LineReader lineReader;

    public StdinInputSource(ControllerConsole console, DefaultHistory history) {
        super(console);
        lineReader = LineReaderBuilder.builder()
                .completer(new RemoteCompleter(console))
                .history(history)
                .terminal(ConsoleInputHandler.INSTANCE.getTerminal())
                .build();
    }

    @Override
    public String getLine() {
        return lineReader.readLine().stripTrailing();
    }
}
