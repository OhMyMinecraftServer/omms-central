package net.zhuruoling.console;

import net.zhuruoling.whitelist.WhitelistReader;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.List;
import java.util.Objects;

public class PlayerNameCompleter implements Completer {

    Completer completer;

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        var word = parsedLine.word();
        var words = parsedLine.words();
        var index = words.indexOf(word);
        if (index == -1){
            completer = NullCompleter.INSTANCE;
            completer.complete(lineReader, parsedLine, list);
            return;
        }

        var whitelistName = words.get(index - 1);
        var whitelist = new WhitelistReader().read(whitelistName);
        if (Objects.isNull(whitelist)){
            completer = NullCompleter.INSTANCE;
            completer.complete(lineReader, parsedLine, list);
        }
        else {
            completer = new StringsCompleter(whitelist.getPlayers());
            completer.complete(lineReader, parsedLine, list);
        }
    }
}
