package icu.takeneko.omms.central.controller.console.input;

import icu.takeneko.omms.central.controller.console.ControllerConsole;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.List;

public class RemoteCompleter implements Completer {
    private final ControllerConsole console;

    public RemoteCompleter(ControllerConsole console) {
        this.console = console;
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        String s = parsedLine.line();
        console.complete(s, parsedLine.cursor())
            .thenAccept(suggestions ->
                new StringsCompleter(suggestions)
                    .complete(lineReader, parsedLine, list)
            );
    }
}
