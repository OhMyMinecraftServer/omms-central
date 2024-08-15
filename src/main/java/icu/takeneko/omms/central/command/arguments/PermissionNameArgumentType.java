package icu.takeneko.omms.central.command.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import icu.takeneko.omms.central.permission.Permission;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PermissionNameArgumentType implements ArgumentType<List<Permission>> {

    private PermissionNameArgumentType() {
    }

    @Override
    public List<Permission> parse(StringReader stringReader) throws CommandSyntaxException {
        var word = stringReader.readString();
        try {
            if (word.equals("all")) {
                return Arrays.stream(Permission.values()).toList();
            }
            return Collections.singletonList(Permission.valueOf(word));
        } catch (IllegalArgumentException e) {
            throw new DynamicCommandExceptionType(o ->
                    new LiteralMessage("Permission " + o + " is not a valid permission enum name.")
            ).createWithContext(stringReader, word);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var prefix = builder.getRemaining();
        if (!prefix.isEmpty()) {
            Arrays.stream(Permission.values()).map(Enum::name)
                    .filter(perm -> perm.startsWith(prefix))
                    .forEach(builder::suggest);
        } else {
            Arrays.stream(Permission.values()).map(Enum::name)
                    .forEach(builder::suggest);
            builder.suggest("*");
        }
        return builder.buildFuture();
    }

    public static PermissionNameArgumentType permission() {
        return new PermissionNameArgumentType();
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.stream(Permission.values()).map(Enum::name).toList();
    }
}
