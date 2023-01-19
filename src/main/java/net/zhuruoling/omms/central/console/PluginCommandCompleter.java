package net.zhuruoling.omms.central.console;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.NullCompleter;

import java.util.List;

public class PluginCommandCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        //new StringsCompleter(ConsoleHandler.getLiteralSimplePluginCommands()).complete(lineReader,parsedLine,list);
        new NullCompleter().complete(lineReader, parsedLine, list);
    }
}