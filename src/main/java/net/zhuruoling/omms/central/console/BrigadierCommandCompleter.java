package net.zhuruoling.omms.central.console;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import net.zhuruoling.omms.central.command.CommandManager;
import net.zhuruoling.omms.central.command.CommandSourceStack;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.List;

public class BrigadierCommandCompleter implements Completer {
    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String s = line.line();
        ParseResults<CommandSourceStack> parseResult =
                CommandManager.INSTANCE.getCommandDispatcher()
                        .parse(s, new CommandSourceStack(CommandSourceStack.Source.CONSOLE));
        var ftr = CommandManager.INSTANCE.getCommandDispatcher().getCompletionSuggestions(parseResult);
        ftr.thenAccept(suggestions -> {
            if (suggestions.isEmpty()){
                new StringsCompleter().complete(reader, line, candidates);
            }else {
                new StringsCompleter(suggestions.getList().stream().map(Suggestion::getText).toList()).complete(reader, line, candidates);
            }
        });
    }
}
