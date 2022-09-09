package net.zhuruoling.console;

import net.zhuruoling.controller.ControllerManager;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.List;

public class ControllerCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        var controllers = new java.util.ArrayList<>(ControllerManager.INSTANCE.getControllers().keySet().stream().toList());
        controllers.add("all");
        new StringsCompleter(controllers).complete(lineReader,parsedLine,list);
    }
}
