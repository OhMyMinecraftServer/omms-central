package net.zhuruoling.omms.central.console;

import net.zhuruoling.omms.central.GlobalVariable;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.List;

public class PermissionNameCompleter implements Completer {
    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        new StringsCompleter(GlobalVariable.INSTANCE.getPermissionNames()).complete(lineReader, parsedLine, list);
        //new StringsCompleter()
    }
}
