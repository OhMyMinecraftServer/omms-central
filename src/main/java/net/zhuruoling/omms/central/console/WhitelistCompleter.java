package net.zhuruoling.omms.central.console;

import net.zhuruoling.omms.central.whitelist.WhitelistManager;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.List;


public class WhitelistCompleter implements Completer {
    final @NotNull Completer completer;

    public WhitelistCompleter(){
        completer = new StringsCompleter(WhitelistManager.INSTANCE.getWhitelistNames());
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        completer.complete(lineReader, parsedLine, list);
    }
}
