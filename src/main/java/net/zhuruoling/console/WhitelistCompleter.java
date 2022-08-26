package net.zhuruoling.console;

import net.zhuruoling.whitelist.WhitelistReader;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.ArrayList;
import java.util.List;


public class WhitelistCompleter implements Completer {
    Completer completer;

    public WhitelistCompleter(){
        var whitelists = new WhitelistReader().getWhitelists();
        ArrayList<String> list = new ArrayList<>();
        whitelists.forEach(x -> {
            list.add(x.getName());
        });
        completer = new StringsCompleter(list);
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        completer.complete(lineReader, parsedLine, list);
    }
}
