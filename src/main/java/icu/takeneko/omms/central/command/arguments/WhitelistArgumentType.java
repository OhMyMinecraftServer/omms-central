package icu.takeneko.omms.central.command.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import icu.takeneko.omms.central.whitelist.Whitelist;
import icu.takeneko.omms.central.whitelist.WhitelistManager;
import icu.takeneko.omms.central.whitelist.Whitelist;
import icu.takeneko.omms.central.whitelist.WhitelistManager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WhitelistArgumentType implements ArgumentType<Whitelist> {

    private static final Collection<String> EXAMPLES = List.of("whitelist_name");

    private WhitelistArgumentType() {
    }

    @Override
    public Whitelist parse(StringReader stringReader) throws CommandSyntaxException {
        var s = stringReader.readUnquotedString();
        if (WhitelistManager.INSTANCE.hasWhitelist(s)) {
            return WhitelistManager.INSTANCE.getWhitelist(s);
        } else {
            throw new DynamicCommandExceptionType(o ->
                    new LiteralMessage("Whitelist " + o + " not exist.")
            ).createWithContext(stringReader, s);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var s = builder.getRemaining();
        if (s.isEmpty()){
            WhitelistManager.INSTANCE.getWhitelistNames()
                    .forEach(builder::suggest);
        }else {
            WhitelistManager.INSTANCE.getWhitelistNames()
                    .stream()
                    .filter( it -> it.startsWith(s))
                    .forEach(builder::suggest);
        }
        return builder.buildFuture();
    }

    @Override
    public String toString() {
        return "whitelist()";
    }

    @Override
    public Collection<String> getExamples() {
        return WhitelistArgumentType.EXAMPLES;
    }

    public static WhitelistArgumentType whitelist() {
        return new WhitelistArgumentType();
    }

    public static <S> Whitelist getWhitelist(CommandContext<S> context, String name){
        return context.getArgument(name, Whitelist.class);
    }
}
