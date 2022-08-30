package net.zhuruoling.console;

import net.zhuruoling.main.RuntimeConstants;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.List;

public class PermissionNameCompleter implements Completer {
    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        new StringsCompleter(RuntimeConstants.INSTANCE.getPermissionNames()).complete(lineReader, parsedLine, list);
        //new StringsCompleter()
    }
}
