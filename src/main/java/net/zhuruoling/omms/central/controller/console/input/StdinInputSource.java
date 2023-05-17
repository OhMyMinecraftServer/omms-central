package net.zhuruoling.omms.central.controller.console.input;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import static net.zhuruoling.omms.central.console.ConsoleInputHandler.terminal;

public class StdinInputSource extends InputSource {
    LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();
    @Override
    public String getLine() {
        return lineReader.readLine();
    }
}
