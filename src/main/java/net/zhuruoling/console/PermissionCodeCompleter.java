package net.zhuruoling.console;

import net.zhuruoling.permission.PermissionManager;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.ArrayList;
import java.util.List;

public class PermissionCodeCompleter implements Completer {


    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        ArrayList<String> strings = new ArrayList<>();
        PermissionManager.INSTANCE.getPermissionTable().forEach((integer, ignored) -> strings.add(Integer.toString(integer)));
        new StringsCompleter(strings).complete(lineReader, parsedLine, list);
    }
}
